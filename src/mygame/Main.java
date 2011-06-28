package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Image;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.texture.Texture2D;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import jme3tools.converters.ImageToAwt;

/**
 * Layered Material Test
 * 
 * Terrain setup code copied from TerrainTest.java in the jmeTests library.
 * 
 * @author Brian Babcock
 */
public class Main extends SimpleApplication {

   LayeredMaterial lm;
   int secondsPassed = 0;
   float goaltpf = (1 / (float)40); //goal framerate (in seconds per frame).

   public static void main(String[] args) {
      Main app = new Main();
      app.start();
   }

   @Override
   public void simpleInitApp() {

      DirectionalLight light = new DirectionalLight();
      light.setDirection((new Vector3f(-0.5f, -1f, -0.5f)).normalize());
      rootNode.addLight(light);
      rootNode.setShadowMode(ShadowMode.CastAndReceive);

      lm = new LayeredMaterial(assetManager, 60);

      lm.setTexture("GrassTexture", generateGrassTex());
      lm.setFloat("GrassTexScale", 64f);
      lm.setLayerMasks(generateMasks(60, lm));
      lm.setFloat("Length", 0.4f);
      lm.setFloat("WaveSpeed", 4.0f);
      lm.setFloat("WaveSize", 0.5f);
      lm.setBoolean("Wave", true);
      //lm.setTexture("AlphaMap", assetManager.loadTexture("Textures/GrassAlpha.png"));
      lm.setGrassDistance(40);
      lm.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);

      /*Box b = new Box(Vector3f.ZERO, 1, 1, 1);
      Material mat = new Material(assetManager, "Common/MatDefs/Misc/SolidColor.j3md");
      mat.setColor("m_Color", ColorRGBA.Blue);
      lm.setBaseMaterial(mat);
      lm.setFloat("MaskScale", 2f);
      Geometry geom = new Geometry("Box", b);
      geom.setQueueBucket(Bucket.Transparent);
      geom.setMaterial(lm);
      rootNode.attachChild(geom);
      flyCam.setMoveSpeed(5f);*/

      // TERRAIN TEXTURE material
      /*Material matRock = new Material(assetManager, "Common/MatDefs/Terrain/Terrain.j3md");
      matRock.setBoolean("useTriPlanarMapping", false);
      //matRock.setBoolean("WardIso", true);
      
      // ALPHA map (for splat textures)
      matRock.setTexture("AlphaMap", assetManager.loadTexture("Textures/alphamap.png"));
      
      // HEIGHTMAP image (for the terrain heightmap)
      Texture heightMapImage = assetManager.loadTexture("Textures/mountains512.png");
      
      // GRASS texture
      Texture grass = assetManager.loadTexture("Textures/grass.jpg");
      grass.setWrap(WrapMode.Repeat);
      matRock.setTexture("DiffuseMap", grass);
      matRock.setFloat("DiffuseMap_0_scale", 64f);
      
      // DIRT texture
      Texture dirt = assetManager.loadTexture("Textures/dirt.jpg");
      dirt.setWrap(WrapMode.Repeat);
      matRock.setTexture("DiffuseMap_1", dirt);
      matRock.setFloat("DiffuseMap_1_scale", 16f);
      
      // ROCK texture
      Texture rock = assetManager.loadTexture("Textures/road.jpg");
      rock.setWrap(WrapMode.Repeat);
      matRock.setTexture("DiffuseMap_2", rock);
      matRock.setFloat("DiffuseMap_2_scale", 128f);*/

      // TERRAIN TEXTURE material
      Material matRock = new Material(assetManager, "Common/MatDefs/Terrain/Terrain.j3md");
      matRock.setBoolean("useTriPlanarMapping", false);

      // ALPHA map (for splat textures)
      matRock.setTexture("Alpha", assetManager.loadTexture("Textures/Terrain/splat/alphamap.png"));

      // HEIGHTMAP image (for the terrain heightmap)
      Texture heightMapImage = assetManager.loadTexture("Textures/Terrain/splat/mountains512.png");

      // GRASS texture
      Texture grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
      grass.setWrap(WrapMode.Repeat);
      matRock.setTexture("Tex1", grass);
      matRock.setFloat("Tex1Scale", 64f);

      // DIRT texture
      Texture dirt = assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg");
      dirt.setWrap(WrapMode.Repeat);
      matRock.setTexture("Tex2", dirt);
      matRock.setFloat("Tex2Scale", 16f);

      // ROCK texture
      Texture rock = assetManager.loadTexture("Textures/Terrain/splat/road.jpg");
      rock.setWrap(WrapMode.Repeat);
      matRock.setTexture("Tex3", rock);
      matRock.setFloat("Tex3Scale", 128f);


      lm.setBaseMaterial(matRock);
      //Material mat = new Material(assetManager, "Common/MatDefs/Misc/SolidColor.j3md");
      //mat.setColor("m_Color", ColorRGBA.White);
      //lm.setBaseMaterial(mat);
      lm.setFloat("MaskScale", 256f);

      // CREATE HEIGHTMAP
      AbstractHeightMap heightmap = null;
      try {
         heightmap = new ImageBasedHeightMap(ImageToAwt.convert(heightMapImage.getImage(), false, true, 0), 1f);
         heightmap.load();

      } catch (Exception e) {
         e.printStackTrace();
      }

      /*
       * Here we create the actual terrain. The tiles will be 65x65, and the total size of the
       * terrain will be 513x513. It uses the heightmap we created to generate the height values.
       */
      /**
       * Optimal terrain patch size is 65 (64x64).
       * The total size is up to you. At 1025 it ran fine for me (200+FPS), however at
       * size=2049, it got really slow. But that is a jump from 2 million to 8 million triangles...
       */
      TerrainQuad terrain = new TerrainQuad("terrain", 65, 513, heightmap.getHeightMap());

      List<Camera> cameras = new ArrayList<Camera>();
      cameras.add(getCamera());
      TerrainLodControl control = new TerrainLodControl(terrain, cameras);
      terrain.addControl(control);
      terrain.setMaterial(lm);
      terrain.setModelBound(new BoundingBox());
      terrain.setLocalTranslation(0, -100, 0);
      terrain.setQueueBucket(Bucket.Transparent);
      rootNode.attachChild(terrain);
      cam.setLocation(new Vector3f(15, -75, -35));

      flyCam.setMoveSpeed(20f);
   }

   @Override
   public void simpleUpdate(float tpf) {
      lm.updateTime(timer.getTimeInSeconds());
   }

   @Override
   public void simpleRender(RenderManager rm) {
      //TODO: add render code
   }

   public Texture generateGrassTex() {
      //We need a seed to initialize each layer from.
      //this way, although each layer has LESS points,
      //the remaining points are still in the same position.
      long seed = FastMath.rand.nextLong();
      Random rand = new Random(seed);

      //Generate a texture for the grass
      //Alpha values will be taken from the mask
      //***COMMENT THIS SECTION OUT IF YOU HAVE YOUR OWN GRASS TEX***
      Image grassTexImg = new Image();
      grassTexImg.setFormat(Image.Format.BGR8); //Don't need alpha value
      grassTexImg.setHeight(200);
      grassTexImg.setWidth(200);
      ByteBuffer data2 = ByteBuffer.allocateDirect(200 * 200 * 3);
      for (int i = 0; i < 200 * 200 * 3;) {
         data2.put(i, (byte) (rand.nextInt(20) + 15));  //Blue value
         data2.put(i + 1, (byte) (rand.nextInt(70) + 45)); //Green value
         data2.put(i + 2, (byte) (rand.nextInt(20) + 20));  //Red value
         i += 3;
      }
      grassTexImg.setData(data2);

      //REPLACE WITH YOUR OWN GRASS TEXTURE IF YOU HAVE ONE
      //But remember, alpha values come from the layer masks, not from this texture.
      Texture grassTex = new Texture2D(grassTexImg);

      grassTex.setWrap(Texture.WrapMode.Repeat);
      return grassTex;
   }

   public List<Texture> generateMasks(int numMasks, LayeredMaterial lm) {
      ByteBuffer texBuf3D = ByteBuffer.allocateDirect(200 * 200 * numMasks); //3D Texture data for geo shader
      List<Texture> masks = new ArrayList<Texture>();
      //We need a seed to initialize each layer from.
      //this way, although each layer has LESS points,
      //the remaining points are still in the same position.
      long seed = FastMath.rand.nextLong();
      Random rand = new Random(seed);

      //Load NUM_DIFFERENT_LAYERS alpha maps into the material.
      //Each one has the same pixel pattern, but a few less total pixels.
      //This way, the grass gradually thins as it gets to the top.
      for (int i = 0; i < numMasks; i++) {
         //reinitialize the random generator
         rand.setSeed(seed);

         //Set up the image
         Image grassMask = new Image();
         grassMask.setFormat(Image.Format.Alpha8);
         grassMask.setHeight(200);
         grassMask.setWidth(200);

         //jME image objects store their pixel data in a ByteBuffer.
         //The ByteBuffer is simply four bytes per pixel (for the ABGR image format),
         //In the order specified above (Alpha (transparency),
         //then Blue, then Green, then Red), one byte each.
         //Since this is just Alpha, we only need one byte per pixel.
         ByteBuffer data = ByteBuffer.allocateDirect(200 * 200);

         //Thin the density as it approaches the top layer
         //The bottom layer will have 1000, the top layer 100.
         float density = i / (float) 60;
         int numGrass = (int) (4000 - ((3500 * density) + 500));

         //Generate the points
         for (int j = 0; j < numGrass; j++) {
            int curPoint = rand.nextInt(40000);
            byte pixelVal = (byte) (1 - (density * 255));
            data.put(curPoint, pixelVal); //Alpha value, for transparency.
            texBuf3D.put((i * 40000) + curPoint, pixelVal);
         }

         grassMask.setData(data);
         Texture2D texToAdd = new Texture2D(grassMask);
         texToAdd.setWrap(Texture.WrapMode.Repeat);
         masks.add(texToAdd);
      }
      ArrayList<ByteBuffer> bb3d = new ArrayList<ByteBuffer>();
      bb3d.add(texBuf3D);
      Image tex3DImg = new Image(Format.Alpha8, 200, 200, 60, bb3d);
      Texture3D t3 = new Texture3D(tex3DImg);
      t3.setWrap(WrapMode.Repeat);
      lm.setTexture("ThreeDTex", t3);
      return masks;
   }
}
