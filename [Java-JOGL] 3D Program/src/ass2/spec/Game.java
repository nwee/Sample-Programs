package ass2.spec;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

/**
 * COMP3421 Assignment 2: A 3D World
 *  This program creates a 3D world from a given terrain input.
 *  
 *  Man Lok Ching (z5034802)
 *	Nelson Wee (z3352078)
 * 
 */
public class Game extends JFrame implements GLEventListener, KeyListener{
    private static final String VERTEX_SHADER = "src/ass2/spec/PhongVertexTex.glsl";
    private static final String FRAGMENT_SHADER = "src/ass2/spec/PhongFragmentTex.glsl";
    private final int NUM_TEXTURES = 4;
    private final int SLICES = 360;
    private final int ROADSLICES = 10;
    private final double trunkRadius = 0.2;

	private Terrain myTerrain;
    private float positions[];
    private float normals[];
    private float texCoords[];
    private FloatBuffer posData;
    private FloatBuffer normalData;
    private FloatBuffer texBuffer;
    private FloatBuffer trunkVertexBuffer = FloatBuffer.allocate(SLICES*6*3);
    private FloatBuffer trunkNormalBuffer = FloatBuffer.allocate(SLICES*6*3);
    private FloatBuffer trunkTextureCoords = FloatBuffer.allocate(SLICES*6*2);
    private int maxStacks = 20;
    private int maxSlices = 30;
    private int maxVertices = maxStacks*(maxSlices+1)*2;
    FloatBuffer leavesVerticesBuffer = FloatBuffer.allocate(maxVertices*3);
    FloatBuffer leavesNormalsBuffer = FloatBuffer.allocate(maxVertices*3);
    FloatBuffer leavesTextureCoords = FloatBuffer.allocate(maxVertices*2);
    FloatBuffer avatarVertices = FloatBuffer.allocate(32472);
    FloatBuffer avatarNormals = FloatBuffer.allocate(32472);
    FloatBuffer avatarTextureCoords = FloatBuffer.allocate(21648);
    FloatBuffer roadVertexBuffer;
    FloatBuffer roadNormalBuffer;
    FloatBuffer roadTextureBuffer;
    private int bufferIds[];
    MyTexture myTextures[];
    private int currIndex = 0;
    private int shaderprogram;

    private String textureFileName0 = "src/ass2/spec/terrain.jpg";
    private String textureExt0 = "jpg";
    private String textureFileName1 = "src/ass2/spec/cardboard.jpg";
    private String textureExt1 = "jpg";
    private String textureFileName2 = "src/ass2/spec/wood.jpg";
    private String textureExt2 = "jpg";
    private String textureFileName3 = "src/ass2/spec/checkered.jpg";
    private String textureExt3 = "jpg";

    int texUnitLoc;
       
    //Camera/Avatar positions
    private int angle = 180;
    private int avaAngle = 0;
    private double avaX = 0, avaY = 0, avaZ = 0;
    private double currX = avaX, currY = avaY, currZ = avaZ;
    //toggle commands
    private boolean FPS = false;
    private boolean torchOn = false;
    // Movement magnitude
    private double step = 0.05;
    private int angleChange = 2;
    //sun angles and settings
    private double sunAngle;
    private float t = 0.00f;
    private boolean sunCycle = false;
    // Array to store current state of keys true/false for polling
    private boolean[] keys = new boolean[120];
    
    //List of other locations
    private ArrayList<float[]> others;

    public Game(Terrain terrain) {
    	super("Assignment 2");
        myTerrain = terrain;

        //get terrain into vertex position array
        generateTerrain();
        generateTrees();
        generateLeaves();
        generateAvatar();
        generateRoads();
        generateOthers();

        posData = Buffers.newDirectFloatBuffer(positions);
        normalData = Buffers.newDirectFloatBuffer(normals);
        texBuffer = Buffers.newDirectFloatBuffer(texCoords);
        bufferIds = new int[5];

        //initialize sun angle
        sunAngle = - Math.atan(myTerrain.getSunlight()[1]/myTerrain.getSunlight()[0]);
    }
    
    /** 
     * Run the game.
     *
     */
    public void run() {
    	  GLProfile glp = GLProfile.getDefault();
          GLCapabilities caps = new GLCapabilities(glp);
          GLJPanel panel = new GLJPanel(caps);
          panel.addGLEventListener(this);
          panel.addKeyListener(this);

          // Add an animator to call 'display' at 60fps        
          FPSAnimator animator = new FPSAnimator(60);
          animator.add(panel);
          animator.start();

          getContentPane().add(panel);
          setSize(800, 600);        
          setVisible(true);
          setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    
    /**
     * Load a level file and display it.
     *
     * @param args - The first argument is a level file in JSON format
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        Terrain terrain = LevelIO.load(new File(args[0]));
        Game game = new Game(terrain);
        game.run();
    }

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        camSetup(gl);

        gl.glUseProgram(shaderprogram);
        gl.glUniform1i(texUnitLoc , 0);

        //turn on backface culling
        gl.glEnable(GL.GL_CULL_FACE);

        // Enable lighting
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_NORMALIZE );

        //control sun cycle with a pseudo-timer
        if (sunCycle) {
        	if (t < 100.00f) {
        		sunAngle += 0.01;
        	}
        	else t=0;
        	t += 0.01;
        }
        
        // Light property vectors.
        float sunAmount = (float) Math.sin(sunAngle);
        float lightDif = (float) Math.abs(Math.sin(sunAngle));
        float lightAmb[] = { 0.0f, 0.0f, 0.0f, 1.0f };

        //calculating light amount, for sun position
        float lightAmount;
        float a = 0.2f;
        if (sunAmount > 0) {
            a += sunAmount * 0.2;
            lightAmount = 1;
        } else {
            lightAmount = 1f - 5 * (float) Math.abs(sunAmount);
            if (lightAmount < 0) {
                lightAmount = 0;
            }
        }

        //set sky color
        gl.glClearColor(0.2f * lightAmount, 0.2f * lightAmount, 0.2f * lightAmount, 1f);

        //set sunlight amount
        float lightDifAndSpec[] = { 1.0f, 0.5f + 0.5f *(float) Math.sin(sunAngle), 0.2f + 0.8f *(float) Math.sin(sunAngle), lightAmount };

        //calculate sunlight position
        float lightPos[] = {(float) -Math.cos(sunAngle), (float) Math.sin(sunAngle), 0f, 0f };

        if (torchOn) { //low light mode
        	a = 0.2f;
        	lightDifAndSpec[0]=lightDifAndSpec[1]=lightDifAndSpec[2]=0f;
        }
                
        // Light properties.
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, lightAmb,0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, lightDifAndSpec,0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, lightDifAndSpec,0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos, 0);

        //Ambient light properties
        float[] globAmb = new float[]{ a, a, a, 1.0f };

        //gl.glEnable(GL2.GL_LIGHT0); // Enable particular light source.
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, globAmb,0); // Global ambient light.
        gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL2.GL_TRUE); // Enable two-sided lighting.
        gl.glLightModeli(GL2.GL_LIGHT_MODEL_COLOR_CONTROL, GL2.GL_SEPARATE_SPECULAR_COLOR);
        
        drawTerrain(gl);
        drawTrees(gl);
        getInput();	//checks the array for keypressed
        if (!FPS)drawAvatar(gl);
        drawTorch(gl);
        drawRoads(gl);
        drawOthers(gl);
	}
	
	/**
	 * Method to handle camera modes   
	 * @param gl
	 */
	public void camSetup(GL2 gl) {
        GLU glu = new GLU();
		//Z to set FPS mode, X to set 3PS mode
        
        if (FPS) {
        	//Rotates at Y axis
        	gl.glRotated(-angle, 0, 1, 0); 
        	//Moves the camera
        	gl.glTranslated(-currX, -currY-myTerrain.altitude(currX,currZ)-.5f, -currZ);
        }
        else {        	
        	//Downward angle
        	gl.glRotated(8, 1, 0, 0);
        	//Offset 180 deg so camera is behind Avatar
        	gl.glRotated(180-avaAngle, 0, 1, 0);
        	// fixed 3 tall 8 far at 20deg angle
        	currX = avaX + 2*Math.sin(Math.toRadians(-(180-avaAngle)));
		    currZ = avaZ + 2*Math.cos(Math.toRadians(-(180-avaAngle)));
		    
        	gl.glTranslated(-currX, -avaY-.73d-myTerrain.altitude(avaX,avaZ), -currZ);
        }
       //System.out.println(myTerrain.altitude(currX,currZ)) ;

	}
	
	@Override
	public void dispose(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
        gl.glDeleteBuffers(1, bufferIds, 0);
	}

	@Override
	public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glEnable(GL2.GL_DEPTH_TEST);

        //enable OpenGL texturing
        gl.glEnable(GL2.GL_TEXTURE_2D);

        //initialise textures
        myTextures = new MyTexture[NUM_TEXTURES];
        myTextures[0] = new MyTexture(gl, textureFileName0, textureExt0, true);
        myTextures[1] = new MyTexture(gl, textureFileName1, textureExt1, true);
        myTextures[2] = new MyTexture(gl, textureFileName2, textureExt2, true);
        myTextures[3] = new MyTexture(gl, textureFileName3, textureExt3, true);


        //initialise shaders
        try {
            shaderprogram = Shader.initShaders(gl, VERTEX_SHADER, FRAGMENT_SHADER);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        texUnitLoc = gl.glGetUniformLocation(shaderprogram, "texUnit1");

        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);

        //Generate 2 VBO buffer and get their IDs
        gl.glGenBuffers(5, bufferIds, 0);

        //terrain buffer
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferIds[0]);
        gl.glBufferData(GL2.GL_ARRAY_BUFFER, (positions.length + normals.length + texCoords.length) * Float.SIZE, null, GL2.GL_STATIC_DRAW);
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, 0, positions.length * Float.SIZE, posData);
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, positions.length * Float.SIZE, normals.length * Float.SIZE, normalData);
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, (positions.length + normals.length) * Float.SIZE, texCoords.length * Float.SIZE, texBuffer);

        //tree trunk buffer
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferIds[1]);
        gl.glBufferData(GL2.GL_ARRAY_BUFFER, (trunkVertexBuffer.capacity() + trunkNormalBuffer.capacity() + trunkTextureCoords.capacity()) * Float.SIZE, null, GL2.GL_STATIC_DRAW);
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, 0, trunkVertexBuffer.capacity() * Float.SIZE, trunkVertexBuffer);
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, trunkVertexBuffer.capacity() * Float.SIZE, trunkNormalBuffer.capacity() * Float.SIZE, trunkNormalBuffer);
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, (trunkVertexBuffer.capacity() + trunkNormalBuffer.capacity()) * Float.SIZE, trunkTextureCoords.capacity() * Float.SIZE, trunkTextureCoords);

        //leaves buffer
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferIds[2]);
        gl.glBufferData(GL2.GL_ARRAY_BUFFER, (leavesVerticesBuffer.capacity() + leavesVerticesBuffer.capacity() + leavesTextureCoords.capacity()) * Float.SIZE, null, GL2.GL_STATIC_DRAW);
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, 0, leavesVerticesBuffer.capacity() * Float.SIZE, leavesVerticesBuffer);
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, leavesVerticesBuffer.capacity() * Float.SIZE, leavesNormalsBuffer.capacity() * Float.SIZE, leavesNormalsBuffer);
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, (leavesVerticesBuffer.capacity() + leavesNormalsBuffer.capacity()) * Float.SIZE, leavesTextureCoords.capacity() * Float.SIZE, leavesTextureCoords);

        //avatar buffer
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferIds[3]);
        gl.glBufferData(GL2.GL_ARRAY_BUFFER, (avatarVertices.capacity() + avatarNormals.capacity() + avatarTextureCoords.capacity()) * Float.SIZE, null, GL2.GL_STATIC_DRAW);
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, 0, avatarVertices.capacity() * Float.SIZE, avatarVertices);
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, avatarVertices.capacity() * Float.SIZE, avatarNormals.capacity() * Float.SIZE, avatarNormals);
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, (avatarVertices.capacity() + avatarNormals.capacity()) * Float.SIZE, avatarTextureCoords.capacity() * Float.SIZE, avatarTextureCoords);

        //roads buffer
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferIds[4]);
        gl.glBufferData(GL2.GL_ARRAY_BUFFER, (roadVertexBuffer.capacity() + roadNormalBuffer.capacity() + roadTextureBuffer.capacity()) * Float.SIZE, null, GL2.GL_STATIC_DRAW);
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, 0, roadVertexBuffer.capacity() * Float.SIZE, roadVertexBuffer);
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, roadVertexBuffer.capacity() * Float.SIZE, roadNormalBuffer.capacity() * Float.SIZE, roadNormalBuffer);
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, (roadVertexBuffer.capacity() + roadNormalBuffer.capacity()) * Float.SIZE, roadTextureBuffer.capacity() * Float.SIZE, roadTextureBuffer);


	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glViewport(0,0,width,height);
        //gl.glOrtho(-10, 20, -10, 20, -10, 109);
        GLU glu = new GLU();

        //glu.gluPerspective(30, (float) width / (float) height, 0.01, 50.0);
        glu.gluPerspective(45, (float) width / (float) height, 0.1, 20.0);
        //glu.gluLookAt(5, 10, 5, 5, 0, 5, 1,0,0);
        
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

	}
	
    /*
     * DRAWING METHODS
     */
	
	/**
	 * A method to draw the terrain
	 * @param gl
	 */
    public void drawTerrain(GL2 gl) {

        // Set current texture
        gl.glActiveTexture(GL2.GL_TEXTURE0);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[currIndex].getTextureId());

        //Set wrap mode for texture in S direction
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
        //Set wrap mode for texture in T direction
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);


        //enable vertex array
        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
        gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);

        //material stuff
        float ambient[] = {0.33f, 0.3f, 1f, 1f};
        float diffuse[] = {0.5f, 0.78f, 0.11f, 1f};
        float specular[] = {0f, 0.0f, 0f, 0f};
        float shininess = 0f;

        gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL2.GL_TRUE);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, ambient,0);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, diffuse,0);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, specular,0);
        gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, shininess);


        //bind buffer we want to use
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferIds[0]);

        gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);
        gl.glNormalPointer(GL.GL_FLOAT, 0, positions.length * Float.SIZE);
        gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, (positions.length + normals.length) * Float.SIZE);
        gl.glDrawArrays(GL2.GL_TRIANGLES, 0, positions.length/3);

        //gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);

        //unbind buffers
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
    }

    /**
     *  A method for drawing trees
      * @param gl
     */
    private void drawTrees(GL2 gl) {

        //user shader program
        gl.glUseProgram(shaderprogram);
        gl.glUniform1i(texUnitLoc , 0);

        // iterate through and draw trees at their locations
        for (Tree tree: myTerrain.trees()) {

            //save matrix
            gl.glPushMatrix();

            //get tree position
            double x = tree.getPosition()[0];
            double z = tree.getPosition()[2];

            //calculate y position on tree
            double result = Math.min(Math.min(myTerrain.altitude(x + trunkRadius, z), myTerrain.altitude(x, z + trunkRadius) ),
                                     Math.min(myTerrain.altitude(x - trunkRadius, z), myTerrain.altitude(x, z - trunkRadius)));

            // translate tree
            gl.glTranslated(tree.getPosition()[0], result, tree.getPosition()[2]);

            // Set current texture
            gl.glActiveTexture(GL2.GL_TEXTURE0);
            gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[2].getTextureId());

            // Set wrap mode for texture in S direction
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
            // Set wrap mode for texture in T direction
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);

            //enable vertex array
            gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
            gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);

            //material stuff
            float ambient[] = {1f, 1f, 1f, 1f};
            float diffuse[] = {1f, 1f, 1f, 1f};
            float specular[] = {0f, 0.0f, 0f, 0f};
            float shininess = 0f;

            gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL2.GL_TRUE);
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, ambient,0);
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, diffuse,0);
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, specular,0);
            gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, shininess);

            //bind buffer we want to use
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferIds[1]);

            gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);
            gl.glNormalPointer(GL.GL_FLOAT, 0, trunkVertexBuffer.capacity() * Float.SIZE);
            gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, (trunkVertexBuffer.capacity() + trunkNormalBuffer.capacity()) * Float.SIZE);
            gl.glDrawArrays(GL2.GL_TRIANGLES, 0, trunkVertexBuffer.capacity()/3);
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

            // translate tree in y direction
            gl.glTranslated(0, 2.5, 0);

            // set texture to use
            gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[0].getTextureId());

            // Draw Leaves
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferIds[2]);
            gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0); //last num is the offset
            gl.glNormalPointer(GL.GL_FLOAT, 0, maxVertices * 3 * Float.SIZE);
            gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, (leavesVerticesBuffer.capacity() + leavesNormalsBuffer.capacity()) * Float.SIZE);
            gl.glDrawArrays(GL2.GL_TRIANGLE_STRIP,0,maxVertices);

            //unbind buffers
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
            gl.glPopMatrix();
        }
    }

    /**
     * A method to draw the Avatar
     * @param gl
     */
    private void drawAvatar(GL2 gl) {
        //save Matrix
        gl.glPushMatrix();

        // Set current texture
        gl.glActiveTexture(GL2.GL_TEXTURE0);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[3].getTextureId());

        //Set wrap mode for texture in S direction
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
        //Set wrap mode for texture in T direction
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);

        //enable vertex array
        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
        gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);

        // Move Avatar
        gl.glTranslated(avaX, avaY+myTerrain.altitude(avaX,avaZ), avaZ);
        gl.glRotated(avaAngle, 0, 1, 0);

        //material stuff
        float ambient[] = {1, 1, 1f, 1f};
        float diffuse[] = {1f, 1f, 1f, 1f};
        float specular[] = {0f, 0.0f, 0f, 0f};
        float shininess = 0f;

        gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL2.GL_TRUE);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, ambient,0);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, diffuse,0);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, specular,0);
        gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, shininess);

        //bind buffer we want to use
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferIds[3]);

        // load VBO and draw
        gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);
        gl.glNormalPointer(GL.GL_FLOAT, 0, avatarNormals.capacity() * Float.SIZE);
        gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, (avatarVertices.capacity() + avatarNormals.capacity()) * Float.SIZE);
        gl.glDrawArrays(GL2.GL_TRIANGLES, 0, avatarVertices.capacity()/3);


        // unbind buffers
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
        gl.glPopMatrix();
    }
    
    /**
     * Method to set up torch light for player 
     * @param gl
     */
    private void drawTorch(GL2 gl) {        
        //Sets it to to be in front of player
    	double tX, tY, tZ;
        if (!FPS) { //movement in avatar coordinates
	    	tX = avaX + .1*Math.sin(Math.toRadians(avaAngle));
	        tZ = avaZ + .1*Math.cos(Math.toRadians(avaAngle));
	        tY = avaY+ .3f + myTerrain.altitude(avaX,avaZ);
        }
        else { //movement in camera coordinates
        	tX = currX + .1*Math.sin(Math.toRadians(angle-180));
	        tZ = currZ + .1*Math.cos(Math.toRadians(angle-180));
	        tY = currY+ .3f + myTerrain.altitude(currX,currZ);
        }

		float DandS_ON[] = { 1.0f, 1.0f, 1.0f, 1.0f };
		float DandS_OFF[] = { 0f, 0f, 0f, 1f };        
    	GLUT glut = new GLUT();
    	//Turn on torch lights
    	if (torchOn) { 
    		gl.glEnable(GL2.GL_LIGHT1);
            gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, DandS_ON,0);
            gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, DandS_ON,0);
    	}
    	//Turn off torch lights
    	else { 
    		gl.glDisable(GL2.GL_LIGHT1);
    		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, DandS_OFF,0);
            gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, DandS_OFF,0);
    	}
    	
        // Emissive property given to lamp to show light
        gl.glPushMatrix();{        	        	
        	gl.glTranslated(tX, tY, tZ);
            //set to positional light X,Y,X,p=1
        	float light[] = { 0f, 0f, 0f, 1f };
        	gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, light,0);
        	//Change to XZ plane
        	
        	if (!FPS) gl.glRotated(avaAngle, 0.0, 1.0, 0.0);
        	else gl.glRotated(angle-180, 0.0, 1.0, 0.0);
          	//forward facing
        	gl.glRotated(-90, 1.0, 0.0, 0.0);
        	
        	float spotAngle = 20f;
        	//initial direction
           	float spotDirection[] = {0.0f, -1.0f, 0.0f};
        	float spotExponent = 2.0f;
        	/* Helper cone to indicate direction
        	gl.glPushMatrix();{
        		gl.glDisable(GL2.GL_LIGHTING);
        		gl.glRotated(-90.0, 1.0, 0.0, 0.0);
        		gl.glColor3f(1.0f, 1.0f, 1.0f);       		
        		glut.glutWireCone(3.0 * Math.tan( spotAngle/180.0 * Math.PI ), 3.0, 20, 20);
        		gl.glEnable(GL2.GL_LIGHTING);
        	}gl.glPopMatrix();
        	*/ 
        	       	
        	// Spotlight properties including position.       	
        	gl.glLightf(GL2.GL_LIGHT1, GL2.GL_SPOT_CUTOFF, spotAngle);
        	gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPOT_DIRECTION, spotDirection,0);    
        	gl.glLightf(GL2.GL_LIGHT1, GL2.GL_SPOT_EXPONENT, spotExponent);

        	float emmL[] = {1.0f, 1.0f, 1.0f, 1f};
        	//draws sphere in front of avatar
        	float matAmbAndDifL[] = {1.0f, 1.0f, 1.0f, 1.0f};
        	float matShineL[] = { 50.0f };
        	// Material properties of sphere.
        	gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDifL,0);
        	gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShineL,0);
        	gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, emmL,0);
        	
        	if (torchOn && !FPS) { //sphere to simulate lamp 
        		glut.glutSolidSphere(0.15, 20, 20);
        	}
        }gl.glPopMatrix();
        
        float matAmbAndDif[] = {1.0f, 0.0f, 0.0f, 1.0f};
        float matShine[] = { 50.0f };
        float emm[] = {0.0f, 0.0f, 0.0f, 1.0f};
        // Material properties of sphere.
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDif,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShine,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, emm,0);
    }

    /**
     * A method to draw the roads from VBO data
     * @param gl
     */
    private void drawRoads(GL2 gl)  {
        // save matrix
        gl.glPushMatrix();

        // Set current texture
        gl.glActiveTexture(GL2.GL_TEXTURE0);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[1].getTextureId());

        //Set wrap mode for texture in S direction
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
        //Set wrap mode for texture in T direction
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);


        //enable vertex array
        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
        gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);

        //material stuff
        float ambient[] = {0.3f, 0.3f, 0.3f, 1f};
        float diffuse[] = {0.78f, 0.78f, 0.78f, 1f};
        float specular[] = {0f, 0.0f, 0f, 0f};
        float shininess = 0f;

        gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL2.GL_TRUE);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, ambient,0);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, diffuse,0);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, specular,0);
        gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, shininess);

        //bind buffer we want to use
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferIds[4]);

        // draw roads from VBO data
        gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);
        gl.glNormalPointer(GL.GL_FLOAT, 0, roadVertexBuffer.capacity() * Float.SIZE);
        gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, (roadVertexBuffer.capacity() + roadNormalBuffer.capacity()) * Float.SIZE);
        gl.glDrawArrays(GL2.GL_TRIANGLES, 0, roadVertexBuffer.capacity()/3);


        //unbind buffers
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
        gl.glPopMatrix();
    }

    /**
     * A method to draw others from VBO
     * @param gl
     */
    private void drawOthers(GL2 gl) {
        // Set current texture
        gl.glActiveTexture(GL2.GL_TEXTURE0);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[currIndex].getTextureId());

        //Set wrap mode for texture in S direction
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
        //Set wrap mode for texture in T direction
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);


        //enable vertex array
        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
        gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);

        // loop through each otehr
        for(float[] pos : others) {
            gl.glPushMatrix();

            // Move Avatar
            gl.glTranslated(pos[0], myTerrain.altitude(pos[0], pos[1]), pos[1]);
            gl.glRotated(pos[2], 0, 1, 0);

            //material stuff
            float ambient[] = {1f, 1f, 1f, 1f};
            float diffuse[] = {1f, 1f, 1f, 1f};
            float specular[] = {0f, 0.0f, 0f, 0f};
            float shininess = 0f;

            gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL2.GL_TRUE);
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, ambient, 0);
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, diffuse, 0);
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, specular, 0);
            gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, shininess);

            //bind buffer we want to use
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferIds[3]);

            // draw other from VBO
            gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);
            gl.glNormalPointer(GL.GL_FLOAT, 0, avatarNormals.capacity() * Float.SIZE);
            gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, (avatarVertices.capacity() + avatarNormals.capacity()) * Float.SIZE);
            gl.glDrawArrays(GL2.GL_TRIANGLES, 0, avatarVertices.capacity() / 3);

            //unbind buffers
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
            gl.glPopMatrix();
        }
    }

    /*
     * VBO DATA GENERATION METHODS
     */
    
    /**
     * A method to generate Terrain  from data
     */
    private void generateTerrain() {

        //get terrain size
        int terrainHeight = (int) myTerrain.size().getHeight();
        int terrainWidth = (int) myTerrain.size().getHeight();

        //make arrays for data
        positions = new float[terrainHeight * terrainWidth * 18];
        normals = new float[terrainHeight * terrainWidth * 18];
        texCoords = new  float[terrainHeight * terrainWidth * 12];

        //loop through and insert data
        for (int h = 0; h  < terrainHeight - 1; h++) {
            for (int w = 0; w < terrainWidth - 1; w++) {

                //get first index of insertion into vertex and normal array
                int firstIndex = h * (terrainWidth - 1)* 18 + w * 18;
                //get first index of insertion into texture coord array
                int firstTexIndex = h * (terrainWidth -1) * 12 + w * 12;

                //insert vertices
                positions[firstIndex] = w;
                positions[firstIndex + 1] = (float) myTerrain.getGridAltitude(w, h);
                positions[firstIndex + 2] = h;
                positions[firstIndex + 3] = w;
                positions[firstIndex + 4] = (float) myTerrain.getGridAltitude(w, h + 1);
                positions[firstIndex + 5] = h + 1;
                positions[firstIndex + 6] = w + 1;
                positions[firstIndex + 7] = (float) myTerrain.getGridAltitude(w + 1, h);
                positions[firstIndex + 8] = h;

                //insert texture coords
                texCoords[firstTexIndex] = 0;
                texCoords[firstTexIndex + 1] = 0;
                texCoords[firstTexIndex + 2] = 0;
                texCoords[firstTexIndex + 3] = 1;
                texCoords[firstTexIndex + 4] = 1;
                texCoords[firstTexIndex + 5] = 0;

                //calculate normals
                double[] p0, p1, p2;
                p0 = new double[]{positions[firstIndex], positions[firstIndex + 1], positions[firstIndex + 2]};
                p1 = new double[]{positions[firstIndex + 3], positions[firstIndex + 4], positions[firstIndex + 5]};
                p2 = new double[]{positions[firstIndex + 6], positions[firstIndex + 7], positions[firstIndex + 8]};
                double[] normal = normal(p0, p1, p2);

                //insert normals
                normals[firstIndex] = normals[firstIndex + 3] = normals[firstIndex + 6] = (float) normal[0];
                normals[firstIndex + 1] = normals[firstIndex + 4] = normals[firstIndex + 7 ] = (float) normal[1];
                normals[firstIndex + 2] = normals[firstIndex + 5] = normals[firstIndex + 8] = (float) normal[2];

                //insert vertices
                positions[firstIndex + 9] = w + 1;
                positions[firstIndex + 10] = (float) myTerrain.getGridAltitude(w + 1, h);
                positions[firstIndex + 11] = h;
                positions[firstIndex + 12] = w;
                positions[firstIndex + 13] = (float) myTerrain.getGridAltitude(w, h + 1);
                positions[firstIndex + 14] = h + 1;
                positions[firstIndex + 15] = w + 1;
                positions[firstIndex + 16] = (float) myTerrain.getGridAltitude(w + 1, h + 1);
                positions[firstIndex + 17] = h + 1;

                //insert texture coords
                texCoords[firstTexIndex + 6] = 1;
                texCoords[firstTexIndex + 7] = 0;
                texCoords[firstTexIndex + 8] = 0;
                texCoords[firstTexIndex + 9] = 1;
                texCoords[firstTexIndex + 10] = 1;
                texCoords[firstTexIndex + 11] = 1;

                //calculate normals
                p0 = new double[]{positions[firstIndex + 9], positions[firstIndex + 10], positions[firstIndex + 11]};
                p1 = new double[]{positions[firstIndex + 12], positions[firstIndex + 13], positions[firstIndex + 14]};
                p2 = new double[]{positions[firstIndex + 15], positions[firstIndex + 16], positions[firstIndex + 17]};
                normal = normal(p0, p1, p2);

                //insert normals
                normals[firstIndex + 9] = normals[firstIndex + 12] = normals[firstIndex + 15] = (float) normal[0];
                normals[firstIndex + 10] = normals[firstIndex + 13] = normals[firstIndex + 16] = (float) normal[1];
                normals[firstIndex + 11] = normals[firstIndex + 14] = normals[firstIndex + 17] = (float) normal[2];
            }
        }
    }

    /**
     * Method to generate tree VBO data
     */
    private void generateTrees() {
        //options for tree
        double radius = trunkRadius;
        float height = 2.5f;

        //variables for angle and points
        int ang;
        float x1, x2, z1, z2;
        int delang = 360/SLICES;

        //generate points
        for (int i = 0; i < SLICES; i++) {

            //calcualte position of points
            ang = i * delang;
            x1 = (float) (radius * Math.cos(Math.toRadians(ang)));
            z1 = (float) (radius * Math.sin(Math.toRadians(ang)));
            x2 = (float) (radius * Math.cos(Math.toRadians(ang+delang)));
            z2 = (float) (radius * Math.sin(Math.toRadians(ang+delang)));

            //insert vertices
            trunkVertexBuffer.put(x1);
            trunkVertexBuffer.put(0);
            trunkVertexBuffer.put(z1);
            trunkVertexBuffer.put(x1);
            trunkVertexBuffer.put(height);
            trunkVertexBuffer.put(z1);
            trunkVertexBuffer.put(x2);
            trunkVertexBuffer.put(height);
            trunkVertexBuffer.put(z2);
            trunkVertexBuffer.put(x2);
            trunkVertexBuffer.put(0);
            trunkVertexBuffer.put(z2);
            trunkVertexBuffer.put(x1);
            trunkVertexBuffer.put(0);
            trunkVertexBuffer.put(z1);
            trunkVertexBuffer.put(x2);
            trunkVertexBuffer.put(height);
            trunkVertexBuffer.put(z2);

            //calculate normals
            double[] normal0 = {x1, 0, z1};
            double[] normal1 = {x2, 0 ,z2};
            normal0 = normalise(normal0);
            normal1 = normalise(normal1);

            //add normals
            trunkNormalBuffer.put((float) normal0[0]);
            trunkNormalBuffer.put((float) normal0[1]);
            trunkNormalBuffer.put((float) normal0[2]);
            trunkNormalBuffer.put((float) normal0[0]);
            trunkNormalBuffer.put((float) normal0[1]);
            trunkNormalBuffer.put((float) normal0[2]);
            trunkNormalBuffer.put((float) normal1[0]);
            trunkNormalBuffer.put((float) normal1[1]);
            trunkNormalBuffer.put((float) normal1[2]);
            trunkNormalBuffer.put((float) normal1[0]);
            trunkNormalBuffer.put((float) normal1[1]);
            trunkNormalBuffer.put((float) normal1[2]);
            trunkNormalBuffer.put((float) normal0[0]);
            trunkNormalBuffer.put((float) normal0[1]);
            trunkNormalBuffer.put((float) normal0[2]);
            trunkNormalBuffer.put((float) normal1[0]);
            trunkNormalBuffer.put((float) normal1[1]);
            trunkNormalBuffer.put((float) normal1[2]);

            //add texture codes for trunk
            trunkTextureCoords.put((float) i/SLICES);
            trunkTextureCoords.put(0);
            trunkTextureCoords.put((float) i/SLICES);
            trunkTextureCoords.put(height);
            trunkTextureCoords.put((float) (i + 1)/SLICES);
            trunkTextureCoords.put(height);
            trunkTextureCoords.put((float) i/SLICES);
            trunkTextureCoords.put(0);
            trunkTextureCoords.put((float) (i + 1) / SLICES);
            trunkTextureCoords.put(0);
            trunkTextureCoords.put((float) (i + 1)/SLICES);
            trunkTextureCoords.put(height);

        }
        //rewind buffers
        trunkVertexBuffer.rewind();
        trunkNormalBuffer.rewind();
        trunkTextureCoords.rewind();
    }
    
    /**
     * A method to generate leaves
     */
    private void generateLeaves() {

        // variables for calculating vertex position
        double deltaT;
        deltaT = 0.5/maxStacks;
        int ang;
        int delang = 360/maxSlices;
        double x1,x2,z1,z2,y1,y2;
        double radius = 1;

        // generate vertices
        for (int i = 0; i < maxStacks; i++)
        {
            double t = -0.25 + i*deltaT;

            for(int j = 0; j <= maxSlices; j++)
            {

                //calcualte vertice position
                ang = j*delang;
                x1=radius * r(t)*Math.cos((double)ang*2.0*Math.PI/360.0);
                x2=radius * r(t+deltaT)*Math.cos((double)ang*2.0*Math.PI/360.0);
                y1 = radius * getY(t);

                z1=radius * r(t)*Math.sin((double)ang*2.0*Math.PI/360.0);
                z2= radius * r(t+deltaT)*Math.sin((double)ang*2.0*Math.PI/360.0);
                y2 = radius * getY(t+deltaT);

                //calculate normal
                double normal[] = {x1,y1,z1};
                normalise(normal);

                // insert vertex
                leavesVerticesBuffer.put((float) x1);
                leavesVerticesBuffer.put((float) y1);
                leavesVerticesBuffer.put((float) z1);

                // insert normal
                leavesNormalsBuffer.put((float) normal[0]);
                leavesNormalsBuffer.put((float) normal[1]);
                leavesNormalsBuffer.put((float) normal[2]);

                // insert texture coord
                leavesTextureCoords.put((float) (Math.asin(normal[0])/Math.PI + 0.5));
                leavesTextureCoords.put((float) (Math.asin(normal[2])/Math.PI + 0.5));

                //calcualte normal
                normal[0] = x2;
                normal[1] = y2;
                normal[2] = z2;
                normalise(normal);

                // insert vertex
                leavesVerticesBuffer.put((float) x2);
                leavesVerticesBuffer.put((float) y2);
                leavesVerticesBuffer.put((float) z2);

                // insert normal
                leavesNormalsBuffer.put((float) normal[0]);
                leavesNormalsBuffer.put((float) normal[1]);
                leavesNormalsBuffer.put((float) normal[2]);

                // insert texture coords
                leavesTextureCoords.put((float) (Math.asin(normal[0])/Math.PI + 0.5));
                leavesTextureCoords.put((float) (Math.asin(normal[2])/Math.PI + 0.5));

            }
        }
        leavesVerticesBuffer.rewind();
        leavesNormalsBuffer.rewind();
        leavesTextureCoords.rewind();
    }

    /**
     * A method to generate Avatar VBO
     */
    private void generateAvatar() {
        // get list of vertices and put in buffer
        File file = new File("src/ass2/spec/legoManVertices.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                avatarVertices.put(Float.parseFloat(line));
            }
            avatarVertices.rewind();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //get list of normals and put into buffer
        file = new File("src/ass2/spec/legoManNormals.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                avatarNormals.put(Float.parseFloat(line));
            }
            avatarNormals.rewind();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // get list of texture coords and put into buffer
        file = new File("src/ass2/spec/legoManTexture.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                avatarTextureCoords.put(Float.parseFloat(line));
            }
            avatarTextureCoords.rewind();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to generate road VBO data
     */
    private void generateRoads() {

        // offset from terrain
        double offset = 0.001;

        // calculate segments for buffer size
        int segments = 0;
        for (Road road : myTerrain.roads()) {
            segments += road.size();
        }
        segments *= SLICES;

        // initialise buffers
        roadVertexBuffer = FloatBuffer.allocate(segments * 18 * ROADSLICES);
        roadNormalBuffer = FloatBuffer.allocate(segments * 18 * ROADSLICES);
        roadTextureBuffer = FloatBuffer.allocate(segments * 12 * ROADSLICES);

        // loop through each raod and generate data
        for (Road road : myTerrain.roads()) {
            for(double i = 0; i < road.size(); i++) {

                // get control Points
                double[] CP0 = road.controlPoint((int)i * 3);
                double[] CP1 = road.controlPoint((int)i * 3 + 1);
                double[] CP2 = road.controlPoint((int)i * 3 + 2);
                double[] CP3 = road.controlPoint((int)i * 3 + 3);

                // loop through each slice and generate data
                for (int j = 0; j < SLICES; j++) {

                    // get t
                    double t = i + (double) j/SLICES;

                    // get point on T
                    double[] PT = road.point(t);

                    // get t between 0 and 1
                    t = t - Math.floor(t);

                    // calcualte tangent to T
                    double[] tangent = {0,0,0};
                    tangent[0] = 3 * (Math.pow(1f - t, 2) * (CP1[0] - CP0[0])
                            + 2 * t * (1f -t)  * (CP2[0] - CP1[0])
                            + Math.pow(t, 2) * (CP3[0] - CP2[0]));
                    tangent[1] = 3 * (Math.pow(1f - t, 2) * (CP1[1] - CP0[1])
                            + 2 * t * (1f -t)  * (CP2[1] - CP1[1])
                            + Math.pow(t, 2) * (CP3[1] - CP2[1]));

                    // calculate normals to tangent of T and normalise
                    double[] normal0 = {-tangent[1], tangent[0], 0};
                    normal0 = normalise(normal0);
                    double[] normal1 = {tangent[1], -tangent[0], 0};
                    normal1 = normalise(normal1);

                    // get road widths
                    double width = road.width();
                    double halfWidth = width/2;

                    // get point on side of road
                    double[] p0 = {PT[0] + halfWidth * normal0[0], 0, PT[1] + halfWidth * normal0[1]};

                    // offset road to height of terrain
                    p0[1] = myTerrain.altitude(PT[0], PT[1]) + offset;

                    // get t of next section
                    t = i + (double) (j + 1) / SLICES;

                    // get point on T
                    PT = road.point(t);

                    // set T between 0 and 1
                    if(t != i + 1) {
                        t = t - Math.floor(t);
                    } else {
                        t = 1;
                    }

                    // calculate tangent to T
                    tangent = new double[]{0,0,0};
                    tangent[0] = 3 * (Math.pow(1f - t, 2) * (CP1[0] - CP0[0])
                            + 2 * t * (1f -t)  * (CP2[0] - CP1[0])
                            + Math.pow(t, 2) * (CP3[0] - CP2[0]));
                    tangent[1] = 3 * (Math.pow(1f - t, 2) * (CP1[1] - CP0[1])
                            + 2 * t * (1f -t)  * (CP2[1] - CP1[1])
                            + Math.pow(t, 2) * (CP3[1] - CP2[1]));

                    // calculate normals of tangent of T
                    double[] normal2 = new double[]{-tangent[1], tangent[0], 0};
                    normal2 = normalise(normal2);
                    double[] normal3 = new double[]{tangent[1], -tangent[0], 0};
                    normal3 = normalise(normal3);

                    // get point on side of road
                    double[] p1 = {PT[0] + halfWidth * normal2[0], 0, PT[1] + halfWidth * normal2[1]};

                    // offset road to height of terrain
                    p1[1] = myTerrain.altitude(PT[0], PT[1]) + offset;

                    // loop throug the width slices and generate points
                    for (int k = 0; k < ROADSLICES; k++) {

                        // get extrude widths
                        double k0 = (double) k/ROADSLICES * width;
                        double k1 = (double) (k + 1) / ROADSLICES * width;

                        //calcualte positions of points
                        double[] p1v = {p0[0] + k0 * normal1[0], p0[1], p0[2] + k0 * normal1[1]};
                        double[] p0v = {p0[0] + k1 * normal1[0], p0[1], p0[2] + k1 * normal1[1]};
                        double[] p3v = {p1[0] + k0 * normal3[0], p1[1], p1[2] + k0 * normal3[1]};
                        double[] p2v = {p1[0] + k1 * normal3[0], p1[1], p1[2] + k1 * normal3[1]};

                        // insert vertices
                        roadVertexBuffer.put((float) p2v[0]);
                        roadVertexBuffer.put((float) p2v[1]);
                        roadVertexBuffer.put((float) p2v[2]);
                        roadVertexBuffer.put((float) p0v[0]);
                        roadVertexBuffer.put((float) p0v[1]);
                        roadVertexBuffer.put((float) p0v[2]);
                        roadVertexBuffer.put((float) p1v[0]);
                        roadVertexBuffer.put((float) p1v[1]);
                        roadVertexBuffer.put((float) p1v[2]);
                        roadVertexBuffer.put((float) p1v[0]);
                        roadVertexBuffer.put((float) p1v[1]);
                        roadVertexBuffer.put((float) p1v[2]);
                        roadVertexBuffer.put((float) p3v[0]);
                        roadVertexBuffer.put((float) p3v[1]);
                        roadVertexBuffer.put((float) p3v[2]);
                        roadVertexBuffer.put((float) p2v[0]);
                        roadVertexBuffer.put((float) p2v[1]);
                        roadVertexBuffer.put((float) p2v[2]);

                        // calculate normals
                        double[] p0n = normal(p2v, p0v, p1v);
                        double[] p1n = normal(p0v, p1v, p3v);
                        double[] p2n = normal(p3v, p2v, p0v);
                        double[] p3n = normal(p1v, p3v, p2v);

                        // insert normals
                        roadNormalBuffer.put((float) p2n[0]);
                        roadNormalBuffer.put((float) p2n[1]);
                        roadNormalBuffer.put((float) p2n[2]);
                        roadNormalBuffer.put((float) p0n[0]);
                        roadNormalBuffer.put((float) p0n[1]);
                        roadNormalBuffer.put((float) p0n[2]);
                        roadNormalBuffer.put((float) p1n[0]);
                        roadNormalBuffer.put((float) p1n[1]);
                        roadNormalBuffer.put((float) p1n[2]);
                        roadNormalBuffer.put((float) p1n[0]);
                        roadNormalBuffer.put((float) p1n[1]);
                        roadNormalBuffer.put((float) p1n[2]);
                        roadNormalBuffer.put((float) p3n[0]);
                        roadNormalBuffer.put((float) p3n[1]);
                        roadNormalBuffer.put((float) p3n[2]);
                        roadNormalBuffer.put((float) p2n[0]);
                        roadNormalBuffer.put((float) p2n[1]);
                        roadNormalBuffer.put((float) p2n[2]);

                        // insert texture coords
                        roadTextureBuffer.put((float) k1);
                        roadTextureBuffer.put((float) i + (float) (j + 1)/ SLICES);
                        roadTextureBuffer.put((float) k1);
                        roadTextureBuffer.put((float) i + (float) j / SLICES);
                        roadTextureBuffer.put((float) k0);
                        roadTextureBuffer.put((float) i + (float) j / SLICES);
                        roadTextureBuffer.put((float) k0);
                        roadTextureBuffer.put(((float) i + (float) j / SLICES));
                        roadTextureBuffer.put((float) k0);
                        roadTextureBuffer.put(((float) i + ((float) j + 1) / SLICES));
                        roadTextureBuffer.put((float) k1);
                        roadTextureBuffer.put(((float) i + ((float) j + 1) / SLICES));
                    }
                }
            }
        }
        // rewind buffers
        roadVertexBuffer.rewind();
        roadNormalBuffer.rewind();
        roadTextureBuffer.rewind();
    }

    /**
     * Method to add list of others
     */
    private void generateOthers() {
        others = new ArrayList<>();
        others.add(new float[]{3,3, 180});
    }

    /*
     * Some maths utility functions
     *
     */
    double [] cross(double u [], double v[]){
        double crossProduct[] = new double[3];
        crossProduct[0] = u[1]*v[2] - u[2]*v[1];
        crossProduct[1] = u[2]*v[0] - u[0]*v[2];
        crossProduct[2] = u[0]*v[1] - u[1]*v[0];

        return crossProduct;
    }

    //Find normal for planar polygon
    public double[] normal(double[] p0, double p1[], double p2[]){
        double [] u = {p1[0] - p0[0], p1[1] - p0[1], p1[2] - p0[2]};
        double [] v = {p2[0] - p0[0], p2[1] - p0[1], p2[2] - p0[2]};
        double [] normal = cross(u,v);
        return normalise(normal);
    }


    double [] normalise(double [] n){
        double  mag = getMagnitude(n);
        double norm[] = {n[0]/mag,n[1]/mag,n[2]/mag};
        return norm;
    }

    double getMagnitude(double [] n){
        double mag = n[0]*n[0] + n[1]*n[1] + n[2]*n[2];
        mag = Math.sqrt(mag);
        return mag;
    }

    public static double[] multiply(double[][] m, double[] v) {

        double[] u = new double[4];

        for (int i = 0; i < 4; i++) {
            u[i] = 0;
            for (int j = 0; j < 4; j++) {
                u[i] += m[i][j] * v[j];
            }
        }

        return u;
    }

    double r(double t){
        double x  = Math.cos(2 * Math.PI * t);
        return x;
    }

    double getY(double t){
        double y  = Math.sin(2 * Math.PI * t);
        return y;
    }
    
    
        
    /** 
     * Keyboard and Movement methods
     */
    //Helper method to poll keyboard presses for smooth movement
    public void getInput() {
    	//camera/Avatar control
		if (keys[KeyEvent.VK_W] || keys[KeyEvent.VK_UP]) {
			if (!FPS) 	move('f', 'a');
	 		else 		move('f', 'c');
		}
		else if (keys[KeyEvent.VK_S] || keys[KeyEvent.VK_DOWN]) { 
			if (!FPS) 	move('b', 'a');
			else 		move('b', 'c');
		}		
		else if (keys[KeyEvent.VK_A] || keys[KeyEvent.VK_LEFT]) { 
			if (!FPS) 	avaAngle = (avaAngle + angleChange) % 360;
			else 		angle = (angle + angleChange) % 360;
		}
		else if (keys[KeyEvent.VK_D] || keys[KeyEvent.VK_RIGHT]) { 
			if (!FPS) 	avaAngle = (avaAngle - angleChange) % 360;
			else 		angle = (angle - angleChange) % 360;
		}
        if (keys[KeyEvent.VK_J]) {
            sunAngle += 0.01;
            System.out.println("sunAngle"+sunAngle);
        }
        else if (keys[KeyEvent.VK_K]) {
            sunAngle -= 0.01;
            System.out.println("sunAngle"+sunAngle);
        }
    }
    
	//Helper function that takes the cmd (f/b) and type (a/c) to determine what to move
	public void move(char cmd, char type) {
		//Avatar movement FPS
		if (type == 'a') {
			if (cmd == 'f') {
				avaX += step*Math.sin(Math.toRadians(avaAngle));
				avaZ += step*Math.cos(Math.toRadians(avaAngle));
			}
			else if (cmd == 'b' ) {
				avaX -= step*Math.sin(Math.toRadians(avaAngle));
			    avaZ -= step*Math.cos(Math.toRadians(avaAngle));
			}
		}
		//Camera movement 3PS
		if (type == 'c') {
			if (cmd == 'f') {
				currX -= step*Math.sin(Math.toRadians(angle));
				currZ -= step*Math.cos(Math.toRadians(angle));
			}
			else if (cmd == 'b' ) {
				currX += step*Math.sin(Math.toRadians(angle));
				currZ += step*Math.cos(Math.toRadians(angle));
			}
		}
		//System.out.println("X:" +avaX + " Y:"+avaY+" Z:"+avaZ);
	}
	
    /* keyPressed listeners
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
	@Override
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;		
		//Keypressed events used for toggling commands
		switch (e.getKeyCode()) {
			//Change view Mode Z = FPS, X = 3PS (default)
			case KeyEvent.VK_Z:
				if (!FPS) { 
					FPS = true; //fps mode
					currX = avaX;
					currY = avaY;
					currZ = avaZ;
					angle = 180+avaAngle;
				}
				break;
			case KeyEvent.VK_X:
				if (FPS) {
					FPS = false;//3PS mode
					avaX = currX;
					avaY = currY;
					avaZ = currZ;
					avaAngle = angle-180;
				}
				break;
			//Toggle torch/night mode
			case KeyEvent.VK_L:
                torchOn = !torchOn;
				System.out.println("light is "+torchOn);
				break;
			//Toggle sun cycle movement
			case KeyEvent.VK_SPACE:
				sunCycle = !sunCycle;
				break;
			//Reset sun angle
			case KeyEvent.VK_H:
				if (!sunCycle) { sunAngle = 0.6f; }
				break;
		}
		
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
