package com.glisco.isometricrenders.util;

import com.glisco.isometricrenders.render.ItemRenderable;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OutputPathBuilder {
    private final ItemRenderable renderable;
    private final String rawFolder;
    private final String rawName;

    public OutputPathBuilder(ItemRenderable renderable, CommandContext<FabricClientCommandSource> context){
        this.renderable = renderable;
        this.rawFolder = StringArgumentType.getString(context, "folder").replaceAll("\\.", "/");
        this.rawName = StringArgumentType.getString(context, "name");
    }

    public ItemRenderable renderable(){
        return renderable;
    }

    @Nullable
    public String appendToRenderable(){
        String nbtData = renderable.stack().getNbt() != null ? renderable.stack().getNbt().asString() : "{}";

        Pattern pattern = Pattern.compile("\\{NBT:([a-zA-Z0-9.\\[\\]]+)\\}");

        try{
            String name = replaceNBTPlaceholders(rawName, nbtData, pattern);
            renderable.exportPath(rawFolder, name);
            return null;
        }catch (IllegalArgumentException iae){
            return iae.getMessage();
        }
    }

    private static String replaceNBTPlaceholders(String s, String nbtData, Pattern pattern) {
        Matcher matcher = pattern.matcher(s);

        while (matcher.find()) {
            String fullPath = matcher.group(0);
            String path = matcher.group(1);
            String value = getValueFromNBTByPath(nbtData, path);
            s = s.replace(fullPath, value);
        }

        return s;
    }

    private static String getValueFromNBTByPath(String nbtJson, String path) {
        JsonElement je = JsonParser.parseString(nbtJson);

        for (String part : path.split("\\.")) {

            if (part.matches(".+\\[\\d+\\]")) {

                int leftBracketIndex = part.indexOf("[");
                int rightBracketIndex = part.indexOf("]");

                if (je.isJsonObject() && je.getAsJsonObject().has(part.substring(0, leftBracketIndex))) {
                    je = je.getAsJsonObject().get(part.substring(0, leftBracketIndex));   // get the array
                } else {
                    throw new IllegalArgumentException("No such field exists in the nbt data: " + part);
                }

                int index;
                try {
                    index = Integer.parseInt(part.substring(leftBracketIndex + 1, rightBracketIndex));   // get index
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid index value for nbt part: " + part);
                }

                if (je.isJsonArray() && je.getAsJsonArray().size() > index) {
                    je = je.getAsJsonArray().get(index);   // get value at index
                } else {
                    throw new IllegalArgumentException("Array index out of bound for nbt part: " + part);
                }

            } else {
                if (je.isJsonObject() && je.getAsJsonObject().has(part)) {
                    je = je.getAsJsonObject().get(part);
                } else {
                    throw new IllegalArgumentException("No such field exists in the nbt data: " + part);
                }
            }
        }

        return je.getAsString();
    }
}
