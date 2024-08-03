package dev.louis.zauber.ritual.mana;

import java.util.Collection;

public record ManaPool(int expectedMana, Collection<ManaReference> manaReferences) {
    public ManaPool(int expectedMana, Collection<ManaReference> manaReferences) {
        this.expectedMana = expectedMana;
        this.manaReferences = manaReferences;
        var insufficientMana = manaReferences.stream().mapToInt(ManaReference::mana).sum() != expectedMana;
        if (insufficientMana)
            throw new IllegalStateException("The ManaPool does not have access to the expected amount of mana.");
    }

    public int totalMana() {
        return manaReferences.stream().mapToInt(ManaReference::mana).sum();
    }

    public boolean isValid() {
       return manaReferences.stream().peek(ManaReference::check).noneMatch(ManaReference::isInvalid);
    }

    public void apply() {
        var invalid = manaReferences.stream().peek(ManaReference::check).anyMatch(ManaReference::isInvalid);
        if (invalid) throw new IllegalStateException("A ManaReference is invaild.");


        for (ManaReference manaReference : manaReferences) {
            manaReference.apply();
        }
    }
}
