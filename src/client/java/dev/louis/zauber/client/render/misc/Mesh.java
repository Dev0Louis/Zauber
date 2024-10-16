package dev.louis.zauber.client.render.misc;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dark on 6/10/2015.
 */
public class Mesh implements Cloneable
{
    public final List<Vector3f> vertices = new ArrayList();
    public final List<Vector2f> textureCoordinates = new ArrayList();
    public final List<Vector3f> normals = new ArrayList();
    public final List<Face> faces = new ArrayList();

    public void addVert(Vector3f pos)
    {
        this.vertices.add(pos);
    }

    public List<Vector3f> getVertices() {
        return vertices;
    }

    public List<Face> getFaces() {
        return faces;
    }

    @Override
    public Mesh clone()
    {
        Mesh mesh = new Mesh();
        mesh.vertices.addAll(vertices);
        mesh.textureCoordinates.addAll(textureCoordinates);
        mesh.normals.addAll(normals);
        for(Face face : faces)
            mesh.faces.add(face.clone());
        return mesh;
    }
}