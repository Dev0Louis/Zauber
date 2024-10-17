package dev.louis.zauber.client.render.misc;

/**
 * Created by Dark on 6/10/2015.
 */
public class Face implements Cloneable
{
    public int vertexX, vertexY, vertexZ;
    public int normalX, normalY, normalZ;
    public int textureCoordinateX, textureCoordinateY, textureCoordinateZ;

    public Face(int p1, int p2, int p3) {
        this.vertexX = p1;
        this.vertexY = p2;
        this.vertexZ = p3;
    }

    @Override
    public Face clone() {
        Face face = new Face(vertexX, vertexY, vertexZ);
        face.normalX = normalX;
        face.normalY = normalY;
        face.normalZ = normalZ;
        face.textureCoordinateX = textureCoordinateX;
        face.textureCoordinateY = textureCoordinateY;
        face.textureCoordinateZ = textureCoordinateZ;
        return face;
    }
}