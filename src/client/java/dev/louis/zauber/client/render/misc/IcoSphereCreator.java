package dev.louis.zauber.client.render.misc;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * http://blog.andreaskahler.com/2009/06/creating-icosphere-mesh-in-code.html
 */
public class IcoSphereCreator
{
    //I know this only saves milli seconds but meh
    private static final HashMap<Integer, Mesh> SPHERE_CACHE = new HashMap();

    private Mesh geometry;
    private int index;
    private HashMap<Long, Integer> middlePointIndexCache;

    public static void init_cache()
    {
        SPHERE_CACHE.clear();
        for (int i = 0; i < 6; i++)
        {
            IcoSphereCreator creator = new IcoSphereCreator();
            SPHERE_CACHE.put(i, creator.createInternal(i));
        }
    }

    /**
     * Creates a new mesh sphere
     * @param level - number of times to subdivide the triangles
     * @return mesh ico sphere, if mesh is cached it will clone
     */
    public static Mesh create(int level)
    {
        return create(level, true);
    }

    /**
     * Creates a new mesh sphere
     * @param level - number of times to subdivide the triangles
     * @param cache - should the mesh be cached for faster creation,
     *                  false if you know your not going to need
     *                  the mesh again
     * @return mesh ico sphere, if mesh is cached it will clone
     */
    public static Mesh create(int level, boolean cache)
    {
        if (!SPHERE_CACHE.containsKey(level) || SPHERE_CACHE.get(level) == null)
        {
            IcoSphereCreator creator = new IcoSphereCreator();
            if(cache)
            {
                SPHERE_CACHE.put(level, creator.createInternal(level));
            }
            else
            {
                return creator.createInternal(level);
            }
        }
        return SPHERE_CACHE.get(level).clone();

    }

    // add vertex to mesh, fix Vertition to be on unit sphere, return index
    private int addVertex(Vector3f p)
    {
        float length = (float) Math.sqrt(p.x() * p.x() + p.y() * p.y() + p.z() * p.z());
        geometry.addVert(new Vector3f(p.x() / length, p.y() / length, p.z() / length));
        return index++;
    }

    // return index of point in the middle of p1 and p2
    private int getMiddlePoint(int p1, int p2)
    {
        // first check if we have it already
        boolean firstIsSmaller = p1 < p2;
        long smallerIndex = firstIsSmaller ? p1 : p2;
        long greaterIndex = firstIsSmaller ? p2 : p1;
        long key = (smallerIndex << 32) + greaterIndex;


        if (this.middlePointIndexCache.containsKey(key))
        {
            return this.middlePointIndexCache.get(key);
        }

        // not in cache, calculate it
        Vector3f point1 = this.geometry.getVertices().get(p1);
        Vector3f point2 = this.geometry.getVertices().get(p2);
        Vector3f middle = new Vector3f(
                (point1.x() + point2.x()) / 2.0f,
                (point1.y() + point2.y()) / 2.0f,
                (point1.z() + point2.z()) / 2.0f);

        // add vertex makes sure point is on unit sphere
        int i = addVertex(middle);

        // store it, return index
        this.middlePointIndexCache.put(key, i);
        return i;
    }

    /**
     * Generates an Iso Sphere using a sub divide process
     * 1 - > 12 vert
     * 2 - > 42 vert
     * 3 - > 62
     * 4 - > 642
     * 5 - > 2562
     * 6 - > 10242
     * 7 - > 40962
     *
     * @param recursionLevel - number of times to sub divide the triangles into 4 more triangles
     * @return Mesh containing the data for the sphere
     */
    private Mesh createInternal(int recursionLevel)
    {
        this.geometry = new Mesh();
        this.middlePointIndexCache = new HashMap();
        this.index = 0;

        // create 12 vertices of a icosahedron
        float t = (float) ((1.0f + Math.sqrt(5.0f)) / 2.0f);

        addVertex(new Vector3f(-1, t, 0));
        addVertex(new Vector3f(1, t, 0));
        addVertex(new Vector3f(-1, -t, 0));
        addVertex(new Vector3f(1, -t, 0));

        addVertex(new Vector3f(0, -1, t));
        addVertex(new Vector3f(0, 1, t));
        addVertex(new Vector3f(0, -1, -t));
        addVertex(new Vector3f(0, 1, -t));

        addVertex(new Vector3f(t, 0, -1));
        addVertex(new Vector3f(t, 0, 1));
        addVertex(new Vector3f(-t, 0, -1));
        addVertex(new Vector3f(-t, 0, 1));


        // create 20 triangles of the icosahedron
        List<Face> faces = new ArrayList();

        // 5 faces around point 0
        faces.add(new Face(0, 11, 5));
        faces.add(new Face(0, 5, 1));
        faces.add(new Face(0, 1, 7));
        faces.add(new Face(0, 7, 10));
        faces.add(new Face(0, 10, 11));

        // 5 adjacent faces
        faces.add(new Face(1, 5, 9));
        faces.add(new Face(5, 11, 4));
        faces.add(new Face(11, 10, 2));
        faces.add(new Face(10, 7, 6));
        faces.add(new Face(7, 1, 8));

        // 5 faces around point 3
        faces.add(new Face(3, 9, 4));
        faces.add(new Face(3, 4, 2));
        faces.add(new Face(3, 2, 6));
        faces.add(new Face(3, 6, 8));
        faces.add(new Face(3, 8, 9));

        // 5 adjacent faces
        faces.add(new Face(4, 9, 5));
        faces.add(new Face(2, 4, 11));
        faces.add(new Face(6, 2, 10));
        faces.add(new Face(8, 6, 7));
        faces.add(new Face(9, 8, 1));


        // refine triangles
        for (int i = 0; i < recursionLevel; i++)
        {
            List<Face> faces2 = new ArrayList();
            for (Face tri : faces)
            {
                // replace triangle by 4 triangles
                int a = getMiddlePoint(tri.vertexX, tri.vertexY);
                int b = getMiddlePoint(tri.vertexY, tri.vertexZ);
                int c = getMiddlePoint(tri.vertexZ, tri.vertexX);

                faces2.add(new Face(tri.vertexX, a, c));
                faces2.add(new Face(tri.vertexY, b, a));
                faces2.add(new Face(tri.vertexZ, c, b));
                faces2.add(new Face(a, b, c));
            }
            faces = faces2;
        }



        /* TODO implement vertex normals
         * vertex v1, v2, v3, ....
         * triangle tr1, tr2, tr3 // all share vertex v1
         * v1.normal = normalize( tr1.normal + tr2.normal + tr3.normal )
         */
        // done, now add triangles to mesh
        this.geometry.getFaces().addAll(faces);
        this.geometry.textureCoordinates.add(new Vector2f(0, 0));
        this.geometry.textureCoordinates.add(new Vector2f(0.5f, 1));
        this.geometry.textureCoordinates.add(new Vector2f(1, 0));

        for (Face face : this.geometry.getFaces())
        {
            Vector3f v1 = geometry.getVertices().get(face.vertexX);
            Vector3f v2 = geometry.getVertices().get(face.vertexY);
            Vector3f v3 = geometry.getVertices().get(face.vertexZ);

            //Generate normals
            //Special thanks to this site https://www.opengl.org/wiki/Calculating_a_Surface_Normal
            Vector3f u = v2.sub(v1, new Vector3f());
            Vector3f v = v3.sub(v1, new Vector3f());
            float x = (u.y() * v.z()) - (u.z() * v.y());
            float y = (u.z() * v.x()) - (u.x() * v.z());
            float z = (u.x() * v.y()) - (u.y() * v.x());
            geometry.normals.add(new Vector3f(x, y, z).normalize());

            face.normalX = geometry.normals.size() - 1;
            face.normalY = geometry.normals.size() - 1;
            face.normalZ = geometry.normals.size() - 1;

            face.textureCoordinateX = 0;
            face.textureCoordinateY = 1;
            face.textureCoordinateZ = 2;
        }

        return this.geometry;
    }
}