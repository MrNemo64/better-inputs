package me.nemo_64.betterinputs.bukkit;

import org.bukkit.Bukkit;

import me.nemo_64.betterinputs.api.BetterInputs;
import me.nemo_64.betterinputs.api.input.modifier.AttemptModifier;
import me.nemo_64.betterinputs.api.input.modifier.TimeoutModifier;
import me.nemo_64.betterinputs.api.util.tick.TickUnit;

public class TestClass {

    public static void test() {
        BetterInputs<?> api = Bukkit.getServicesManager().getRegistration(BetterInputs.class).getProvider();

        api.createInput(String.class).type("betterinputs:input/command_block").provide()
            .withModifier(new TimeoutModifier<>(1, TickUnit.DAY))
            .withModifier(new AttemptModifier<>(3, (string -> string.length() > 5 && !string.isBlank())));

    }

}
