package restudio.reglass.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
//#if MC >= 26
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.platform.CompareOp;
//#else
import com.mojang.blaze3d.platform.DepthTestFunction;
//#endif
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
//#if MC >= 26
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.resources.Identifier;
//#else
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
//#endif

public final class LiquidGlassPipelines {
    private static RenderPipeline LIQUID_GLASS_GUI;

    private LiquidGlassPipelines() {}

    public static synchronized RenderPipeline getGuiPipeline() {
        if (LIQUID_GLASS_GUI == null) {
            RenderPipeline.Builder b = RenderPipeline.builder()
//#if MC >= 26
                    .withLocation(Identifier.fromNamespaceAndPath("reglass", "pipeline/liquid_glass_gui"))
                    .withVertexShader(Identifier.fromNamespaceAndPath("reglass", "core/blit_fullscreen"))
                    .withFragmentShader(Identifier.fromNamespaceAndPath("reglass", "program/liquid_glass_gui"))
//#else
                    .withLocation(Identifier.of("reglass", "pipeline/liquid_glass_gui"))
                    .withVertexShader(Identifier.of("reglass", "core/blit_fullscreen"))
                    .withFragmentShader(Identifier.of("reglass", "program/liquid_glass_gui"))
//#endif
                    .withUniform("Projection", UniformType.UNIFORM_BUFFER)
                    .withUniform("SamplerInfo", UniformType.UNIFORM_BUFFER)
                    .withUniform("CustomUniforms", UniformType.UNIFORM_BUFFER)
                    .withUniform("WidgetInfo", UniformType.UNIFORM_BUFFER)
                    .withUniform("BgConfig", UniformType.UNIFORM_BUFFER)
                    .withSampler("Sampler0")
                    .withSampler("Sampler1")
                    .withSampler("Sampler2")
                    .withSampler("Sampler3")
                    .withSampler("Sampler4")
                    .withSampler("Sampler5")
//#if MC >= 26
                    .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
                    .withVertexFormat(DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS);
//#else
                    .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                    .withDepthWrite(false)
                    .withVertexFormat(VertexFormats.POSITION, VertexFormat.DrawMode.QUADS);
//#endif

            LIQUID_GLASS_GUI = b.build();
            RenderSystem.getDevice().precompilePipeline(LIQUID_GLASS_GUI, null);
        }
        return LIQUID_GLASS_GUI;
    }
}
