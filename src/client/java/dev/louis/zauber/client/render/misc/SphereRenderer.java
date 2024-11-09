package dev.louis.zauber.client.render.misc;


import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Vector3f;

public class SphereRenderer {
    private static final int SPHERE_SEGMENTS = 16;
    private static final int SPHERE_RINGS = 8;

    public static void renderColoredSphere(MatrixStack.Entry entry, VertexConsumer vertexConsumer, float r, float g, float b) {
        Mesh mesh = IcoSphereCreator.create(2, true);


        for (Face face : mesh.faces) {
            vertexConsumer.vertex(entry, mesh.vertices.get(face.vertexZ));
            vertexConsumer.texture(mesh.textureCoordinates.get(face.textureCoordinateZ).x, mesh.textureCoordinates.get(face.textureCoordinateZ).y);
            vertexConsumer.color(r, g, b, 1);
            vertexConsumer.vertex(entry, mesh.vertices.get(face.vertexY));
            vertexConsumer.texture(mesh.textureCoordinates.get(face.textureCoordinateY).x, mesh.textureCoordinates.get(face.textureCoordinateY).y);
            vertexConsumer.color(r, g, b, 1);
            vertexConsumer.vertex(entry, mesh.vertices.get(face.vertexX));
            vertexConsumer.texture(mesh.textureCoordinates.get(face.textureCoordinateX).x, mesh.textureCoordinates.get(face.textureCoordinateX).y);
            vertexConsumer.color(r, g, b, 1);
        }
        for (Face face : mesh.faces) {
            vertexConsumer.vertex(entry, mesh.vertices.get(face.vertexX));
            vertexConsumer.texture(mesh.textureCoordinates.get(face.textureCoordinateX).x, mesh.textureCoordinates.get(face.textureCoordinateX).y);
            vertexConsumer.color(r, g, b, 1);
            vertexConsumer.vertex(entry, mesh.vertices.get(face.vertexY));
            vertexConsumer.texture(mesh.textureCoordinates.get(face.textureCoordinateY).x, mesh.textureCoordinates.get(face.textureCoordinateY).y);
            vertexConsumer.color(r, g, b, 1);
            vertexConsumer.vertex(entry, mesh.vertices.get(face.vertexZ));
            vertexConsumer.texture(mesh.textureCoordinates.get(face.textureCoordinateZ).x, mesh.textureCoordinates.get(face.textureCoordinateZ).y);
            vertexConsumer.color(r, g, b, 1);
        }
    }

    public static void renderSphere(MatrixStack.Entry entry, VertexConsumer vertexConsumer) {
        Mesh mesh = IcoSphereCreator.create(2, true);


        for (Face face : mesh.faces) {
            vertexConsumer.vertex(entry, mesh.vertices.get(face.vertexZ));
            vertexConsumer.texture(mesh.textureCoordinates.get(face.textureCoordinateZ).x, mesh.textureCoordinates.get(face.textureCoordinateZ).y);
            vertexConsumer.vertex(entry, mesh.vertices.get(face.vertexY));
            vertexConsumer.texture(mesh.textureCoordinates.get(face.textureCoordinateY).x, mesh.textureCoordinates.get(face.textureCoordinateY).y);
            vertexConsumer.vertex(entry, mesh.vertices.get(face.vertexX));
            vertexConsumer.texture(mesh.textureCoordinates.get(face.textureCoordinateX).x, mesh.textureCoordinates.get(face.textureCoordinateX).y);
        }
        for (Face face : mesh.faces) {
            vertexConsumer.vertex(entry, mesh.vertices.get(face.vertexX));
            vertexConsumer.texture(mesh.textureCoordinates.get(face.textureCoordinateX).x, mesh.textureCoordinates.get(face.textureCoordinateX).y);
            vertexConsumer.vertex(entry, mesh.vertices.get(face.vertexY));
            vertexConsumer.texture(mesh.textureCoordinates.get(face.textureCoordinateY).x, mesh.textureCoordinates.get(face.textureCoordinateY).y);
            vertexConsumer.vertex(entry, mesh.vertices.get(face.vertexZ));
            vertexConsumer.texture(mesh.textureCoordinates.get(face.textureCoordinateZ).x, mesh.textureCoordinates.get(face.textureCoordinateZ).y);
        }
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
        vertices.texture(0, 1);
        //vertices.color(color);
        //vertices.light(light);
        //vertices.normal(entry, 0, 1, 0);

        vertices.vertex(entry, p2);
        vertices.texture(1, 1);
        //vertices.color(color);
        //vertices.light(light);
        //vertices.normal(entry, 0, 1, 0);
        //vertices.texture(0, 1);

        vertices.vertex(entry, p3);
        vertices.texture(1, 0);
        //vertices.color(color);
        //vertices.light(light);
        //vertices.normal(entry, 0, 1, 0);
        //vertices.texture(1, 0);

        vertices.vertex(entry, p4);
        vertices.texture(0, 0);
        //vertices.color(color);
        //vertices.light(light);
        //vertices.normal(entry, 0, 1, 0);
        //vertices.texture(1, 1);
    }
}