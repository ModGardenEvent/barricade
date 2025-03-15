package net.modgarden.barricade.client.util.dfu.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import house.greenhouse.greenhouseconfig.api.dfu.GreenhouseConfigDFUReferences;

import java.util.Map;
import java.util.function.Supplier;

public class ConfigSchema extends Schema {
    public ConfigSchema(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    @Override
    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
        schema.registerType(true,
                GreenhouseConfigDFUReferences.CONFIG,
                DSL::remainder);
        // Use only the remainder value for this schema, as we are unable to easily remove templates from schemas.
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        return Map.of();
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        return Map.of();
    }
}
