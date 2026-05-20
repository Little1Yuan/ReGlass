package restudio.reglass.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;
import restudio.reglass.client.api.WidgetStyle;
import restudio.reglass.client.config.ReGlassSettingsIO;
import restudio.reglass.mixin.accessor.OptionsAccessor;
import restudio.reglass.client.screen.config.ReGlassConfigScreen;

import java.util.Arrays;

public class ReGlassClient implements ClientModInitializer {
    private static final KeyMapping.Category KEY_CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("reglass", "main"));
    private static final KeyMapping CONFIG_KEY = new KeyMapping("key.reglass.config", InputConstants.UNKNOWN.getValue(), KEY_CATEGORY);
    private static final KeyMapping PLAYGROUND_KEY = new KeyMapping("key.reglass.playground", InputConstants.UNKNOWN.getValue(), KEY_CATEGORY);
    private static boolean keyMappingsRegistered;

    public static Minecraft minecraftClient;

    @Override
    public void onInitializeClient() {
        minecraftClient = Minecraft.getInstance();

        ReGlassSettingsIO.loadIntoMemory();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!keyMappingsRegistered) {
                if (client.options == null) {
                    return;
                }
                registerKeyMappings(client);
            }
            while (CONFIG_KEY.consumeClick()) {
                if (client.screen == null) {
                    client.setScreen(new ReGlassConfigScreen(null));
                }
            }
            while (PLAYGROUND_KEY.consumeClick()) {
                if (client.screen == null) {
                    client.setScreen(new PlaygroundScreen());
                }
            }
        });
    }

    private static void registerKeyMappings(Minecraft client) {
        KeyMapping[] existingMappings = client.options.keyMappings;
        KeyMapping[] mappings = Arrays.copyOf(existingMappings, existingMappings.length + 2);
        mappings[existingMappings.length] = CONFIG_KEY;
        mappings[existingMappings.length + 1] = PLAYGROUND_KEY;
        ((OptionsAccessor) client.options).setKeyMappings(mappings);
        KeyMapping.resetMapping();
        client.options.load();
        keyMappingsRegistered = true;
    }

    public static class PlaygroundScreen extends Screen {
        private boolean blur;

        public PlaygroundScreen() {
            super(Component.literal("ReGlass Playground"));
        }

        @Override
        protected void init() {
            super.init();

            WidgetStyle customStyle = WidgetStyle.create().tint(ChatFormatting.GOLD.getColor(), 0.4f).blurRadius(0).shadow(25f, 0.2f, 0f, 3f).smoothing(.05f).shadowColor(0x000000, 1.0f);
            addRenderableWidget(new LiquidGlassWidget(width / 2 - 75, height / 2 - 25, 150, 50, customStyle).setMoveable(true));
            addRenderableWidget(Button.builder(Component.literal("Toggle BG Blur"), b -> blur = !blur).bounds(10, 10, 120, 20).build());
        }

        @Override
        public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
            context.text(minecraftClient.font, Component.literal("This is a Minecraft Screen"), width / 2 - 70, 10, 0xFFFFFFFF, true);
            super.extractRenderState(context, mouseX, mouseY, delta);
        }

        @Override
        public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
            if (blur) super.extractBackground(context, mouseX, mouseY, delta);
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent click, boolean isDouble) {
            if (click.button() == 1) {
                addRenderableWidget(new LiquidGlassWidget((int) click.x() - 50, (int) click.y() - 50, 100, 100, WidgetStyle.create().smoothing(.05f))).setMoveable(true);
                return true;
            }
            return super.mouseClicked(click, isDouble);
        }
    }
}
