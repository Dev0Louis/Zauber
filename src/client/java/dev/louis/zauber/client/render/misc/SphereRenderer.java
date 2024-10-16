package dev.louis.zauber.client.render.misc;


import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Vector3f;

public class SphereRenderer {
    private static final int SPHERE_SEGMENTS = 16;
    private static final int SPHERE_RINGS = 8;

    public static void renderSphere(MatrixStack.Entry entry, VertexConsumer vertexConsumer) {
        Mesh mesh = IcoSphereCreator.create(2, true);

        for (Face face : mesh.faces) {
            vertexConsumer.vertex(entry, mesh.vertices.get(face.vertexIndices[2]));
            vertexConsumer.texture(mesh.textureCoordinates.get(face.textureCoordinateIndices[2]).x, mesh.textureCoordinates.get(face.textureCoordinateIndices[2]).y);
            vertexConsumer.vertex(entry, mesh.vertices.get(face.vertexIndices[1]));
            vertexConsumer.texture(mesh.textureCoordinates.get(face.textureCoordinateIndices[1]).x, mesh.textureCoordinates.get(face.textureCoordinateIndices[1]).y);
            vertexConsumer.vertex(entry, mesh.vertices.get(face.vertexIndices[0]));
            vertexConsumer.texture(mesh.textureCoordinates.get(face.textureCoordinateIndices[0]).x, mesh.textureCoordinates.get(face.textureCoordinateIndices[0]).y);
        }

        for (Face face : mesh.faces) {
            vertexConsumer.vertex(entry, mesh.vertices.get(face.vertexIndices[0]));
            vertexConsumer.texture(mesh.textureCoordinates.get(face.textureCoordinateIndices[0]).x, mesh.textureCoordinates.get(face.textureCoordinateIndices[0]).y);
            vertexConsumer.vertex(entry, mesh.vertices.get(face.vertexIndices[1]));
            vertexConsumer.texture(mesh.textureCoordinates.get(face.textureCoordinateIndices[1]).x, mesh.textureCoordinates.get(face.textureCoordinateIndices[1]).y);
            vertexConsumer.vertex(entry, mesh.vertices.get(face.vertexIndices[2]));
            vertexConsumer.texture(mesh.textureCoordinates.get(face.textureCoordinateIndices[2]).x, mesh.textureCoordinates.get(face.textureCoordinateIndices[2]).y);
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