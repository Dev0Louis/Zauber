package dev.louis.zauber.client.render.misc;


import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Vector3f;

public class SphereRenderer {
    private static final int SPHERE_SEGMENTS = 16;
    private static final int SPHERE_RINGS = 8;

    public static void renderSphere(MatrixStack.Entry entry, int light, VertexConsumer vertexConsumer) {
        float phiStep = (float) (Math.PI / SPHERE_RINGS);
        float thetaStep = (float) (2.0 * Math.PI / SPHERE_SEGMENTS);

        /*for (int i = 0; i < SPHERE_RINGS; i++) {
            float phi1 = i * phiStep;
            float phi2 = (i + 1) * phiStep;

            for (int j = 0; j < SPHERE_SEGMENTS; j++) {
                float theta1 = j * thetaStep;
                float theta2 = (j + 1) * thetaStep;

                float mul = 2.435f;
                // Calculate the vertices for the current quad
                Vector3f p1 = calculateSpherePoint(phi1, theta1, mul);
                Vector3f p2 = calculateSpherePoint(phi1, theta2, mul);
                Vector3f p3 = calculateSpherePoint(phi2, theta2, mul);
                Vector3f p4 = calculateSpherePoint(phi2, theta1, mul);

                final boolean irisWorkAround = false;
                // Render the quad
                if(irisWorkAround) {
                    //NOT IMPLEMENTED YET!
                    return;
                } else {
                    renderQuad(entry, vertexConsumer, light, p1, p2, p3, p4);
                }
            }
        }*/
        int i = 4;
        float phi1 = i * phiStep;
        float phi2 = (i + 1) * phiStep;

        int j = 4;
        float theta1 = j * thetaStep;
        float theta2 = (j + 1) * thetaStep;


        float mul = 2.435f;
        // Calculate the vertices for the current quad
        Vector3f p1 = calculateSpherePoint(phi1, theta1, mul);
        Vector3f p2 = calculateSpherePoint(phi1, theta2, mul);
        Vector3f p3 = calculateSpherePoint(phi2, theta2, mul);
        Vector3f p4 = calculateSpherePoint(phi2, theta1, mul);

        renderQuad(entry, vertexConsumer, light, p1, p2, p3, p4);
    }

    private static Vector3f calculateSpherePoint(float phi, float theta, float mul) {
        float x = (float) (Math.sin(phi) * Math.cos(theta));
        float y = (float) Math.cos(phi);
        float z = (float) (Math.sin(phi) * Math.sin(theta));
        return new Vector3f(x, y, z).mul(mul);
    }

    private static void renderQuad(
            MatrixStack.Entry entry,
            VertexConsumer vertices,
            int light,
            Vector3f p1,
            Vector3f p2,
            Vector3f p3,
            Vector3f p4
    ) {

        var color = 0xFFFFFFFF;
        vertices.vertex(entry, p1);
        vertices.texture(0, 0);
        vertices.color(color);
        vertices.light(light);
        //vertices.normal(entry, 0, 1, 0);

        vertices.vertex(entry, p2);
        vertices.texture(0, 1);
        vertices.color(color);
        vertices.light(light);
        //vertices.normal(entry, 0, 1, 0);
        //vertices.texture(0, 1);

        vertices.vertex(entry, p3);
        vertices.texture(1, 1);
        vertices.color(color);
        vertices.light(light);
        //vertices.normal(entry, 0, 1, 0);
        //vertices.texture(1, 0);

        vertices.vertex(entry, p4);
        vertices.texture(0, 0);
        vertices.color(color);
        vertices.light(light);
        //vertices.normal(entry, 0, 1, 0);
        //vertices.texture(1, 1);
    }
}