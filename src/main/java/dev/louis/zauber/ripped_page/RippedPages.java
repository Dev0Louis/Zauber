package dev.louis.zauber.ripped_page;

import dev.louis.zauber.Zauber;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RippedPages {
    public static List<RippedPage> rippedPages = new ArrayList<>();


    public static final RippedPage MANA_BOW = register(Identifier.of(Zauber.MOD_ID, "mana_bow"));
    public static final RippedPage EMPTY = register(Identifier.of(Zauber.MOD_ID, "empty"));

    public static RippedPage register(Identifier id) {
        if (rippedPages.stream().anyMatch(rippedPage -> rippedPage.id().equals(id)))
            throw new IllegalStateException("Duplicate Id! " + id);

        var rippedPage = new RippedPage(id);
        rippedPages.add(rippedPage);
        return rippedPage;
    }
}
