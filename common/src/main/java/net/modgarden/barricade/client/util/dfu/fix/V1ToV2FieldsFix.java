package net.modgarden.barricade.client.util.dfu.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import house.greenhouse.greenhouseconfig.api.dfu.GreenhouseConfigDFUReferences;

import java.util.ArrayList;
import java.util.List;

public class V1ToV2FieldsFix extends DataFix {
    public V1ToV2FieldsFix(Schema outputSchema) {
        super(outputSchema, false);
    }

    @Override
    protected TypeRewriteRule makeRule() {
        return fixTypeEverywhereTyped("Fix v1 fields to v2 fields", getInputSchema().getType(GreenhouseConfigDFUReferences.CONFIG), typed -> typed.update(DSL.remainderFinder(), V1ToV2FieldsFix::fixDynamic));
    }

    private static Dynamic<?> fixDynamic(Dynamic<?> dynamic) {
        List<String> oldVisibleBlocks = dynamic.get("visible_blocks").orElseEmptyList().asList(dynamic1 -> dynamic1.asString(""));
        List<Dynamic<?>> newVisibleBlocks = new ArrayList<>();
        for (String value : oldVisibleBlocks) {
            if (value.equals("barricade:barriers") || value.equals("barricade:lights"))
                value = "#" + value;
            newVisibleBlocks.add(dynamic.createString(value));
        }
        return dynamic.set("visible_blocks", dynamic.createList(newVisibleBlocks.stream()));
    }
}