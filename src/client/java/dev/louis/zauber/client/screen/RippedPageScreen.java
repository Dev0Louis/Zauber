//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dev.louis.zauber.client.screen;

import java.util.List;
import java.util.Objects;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.louis.zauber.client.glisco.StencilFramebuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.ClickEvent.Action;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import static com.mojang.blaze3d.platform.GlConst.GL_ALWAYS;
import static org.lwjgl.opengl.GL11C.*;

@Environment(EnvType.CLIENT)
public class RippedPageScreen extends Screen {
    public static final int field_32328 = 16;
    public static final int field_32329 = 36;
    public static final int field_32330 = 30;
    public static final Text EMPTY_PROVIDER = Text.of("HEY LISTEN!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    public static final Identifier RIPPED_PAGE_TEXTURE = Identifier.of("zauber", "textures/gui/ripped_page.png");
    protected static final int MAX_TEXT_WIDTH = 114;
    protected static final int MAX_TEXT_HEIGHT = 128;
    protected static final int WIDTH = 192;
    protected static final int HEIGHT = 192;
    private Text content;
    private List<OrderedText> cachedContent;

    public static StencilFramebuffer stencilFrameBuffer;

    {
        if (stencilFrameBuffer == null) {
            stencilFrameBuffer = new StencilFramebuffer(MinecraftClient.getInstance().getWindow().getFramebufferWidth(), MinecraftClient.getInstance().getWindow().getFramebufferHeight());
        }
    }

    public RippedPageScreen(Text text) {
        this(text, true);
    }

    public RippedPageScreen() {
        this(EMPTY_PROVIDER, false);
    }

    private RippedPageScreen(Text content, boolean playPageTurnSound) {
        super(NarratorManager.EMPTY);
        this.cachedContent = null;
        this.content = content;
    }

    public void setPageProvider(Text pageProvider) {
        this.content = pageProvider;
        this.cachedContent = null;
    }


    protected void init() {
        this.addCloseButton();
    }

    protected void addCloseButton() {
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, (button) -> {
            this.close();
        }).dimensions(this.width / 2 - 100, 196, 200, 20).build());
    }


    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        int i = (this.width - 192) / 2;
        if (cachedContent == null) {
            this.cachedContent = this.textRenderer.wrapLines(this.content, MAX_TEXT_WIDTH);
        }

        int linesToDraw = Math.min(MAX_TEXT_HEIGHT / 9, this.cachedContent.size());

        stencilFrameBuffer.beginWrite(true);
        GL11.glEnable(GL11.GL_STENCIL_TEST);

        RenderSystem.clearStencil(0);
        RenderSystem.clear(GL11.GL_STENCIL_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
        RenderSystem.stencilFunc(GL_ALWAYS, 1, 0xFF);
        RenderSystem.stencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
        context.drawTexture(RIPPED_PAGE_TEXTURE, (this.width - 192) / 2, 2, 0, 0, 192, 192);

        RenderSystem.stencilFunc(GL_EQUAL, 1, 0xFF);
        RenderSystem.stencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
        for(int line = 0; line < linesToDraw; ++line) {
            OrderedText orderedText = this.cachedContent.get(line);
            context.drawText(this.textRenderer, orderedText, i + 36, 32 + line * 9, 0, false);
        }

        stencilFrameBuffer.endWrite();
        RenderSystem.enableBlend();
        MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
        stencilFrameBuffer.draw(stencilFrameBuffer.textureWidth, stencilFrameBuffer.textureHeight, false);
        GL11.glDisable(GL11.GL_STENCIL_TEST);
        RenderSystem.defaultBlendFunc();

        Style style = this.getTextStyleAt(mouseX, mouseY);
        if (style != null) {
            context.drawHoverEvent(this.textRenderer, style, mouseX, mouseY);
        }

    }

    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderInGameBackground(context);

    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            Style style = this.getTextStyleAt(mouseX, mouseY);
            if (style != null && this.handleTextClick(style)) {
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean handleTextClick(Style style) {
        ClickEvent clickEvent = style.getClickEvent();
        if (clickEvent == null) {
            return false;
        } else {
            boolean bl = super.handleTextClick(style);
            if (bl && clickEvent.getAction() == Action.RUN_COMMAND) {
                this.closeScreen();
            }

            return bl;
        }
    }

    protected void closeScreen() {
        this.client.setScreen(null);
    }

    @Nullable
    public Style getTextStyleAt(double x, double y) {
        if (this.cachedContent.isEmpty()) {
            return null;
        } else {
            int i = MathHelper.floor(x - (double)((this.width - 192) / 2) - 36.0);
            int j = MathHelper.floor(y - 2.0 - 30.0);
            if (i >= 0 && j >= 0) {
                Objects.requireNonNull(this.textRenderer);
                int k = Math.min(MAX_TEXT_HEIGHT / 9, this.cachedContent.size());
                if (i <= MAX_TEXT_WIDTH) {
                    Objects.requireNonNull(this.client.textRenderer);
                    if (j < 9 * k + k) {
                        Objects.requireNonNull(this.client.textRenderer);
                        int l = j / 9;
                        if (l >= 0 && l < this.cachedContent.size()) {
                            OrderedText orderedText = this.cachedContent.get(l);
                            return this.client.textRenderer.getTextHandler().getStyleAt(orderedText, i);
                        }

                        return null;
                    }
                }

                return null;
            } else {
                return null;
            }
        }
    }
}
