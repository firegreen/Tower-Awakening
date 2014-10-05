package moteur_graphique;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import jogamp.opengl.gl4.GL4bcImpl;

import com.jogamp.opengl.util.texture.Texture;

abstract class DrawableObject3D {
	
	public Point3D getPosition() {
		return position;
	}
	public static class Point3D{
		
		public Point3D(float x, float y, float z) {
			super();
			this.x = x;
			this.y = y;
			this.z = z;
		}
		public float x,y,z;
		public Point3D(){}
		
	}
	
	public static class Color
	{
		public Color(byte a, byte r, byte g, byte b) {
			super();
			this.a = a;
			this.r = r;
			this.g = g;
			this.b = b;
		}
		
		public Color(byte r, byte g, byte b) {
			this((byte)255,r,g,b);
		}
		
		
		public void giveColor(GL4bcImpl env)
		{
			env.glColor4b(a,r,g,b);
		}
		
		public byte[] getARGB(){ 
			byte[] retour = {a,r,g,b};
			return retour;
		}
		public byte a,r,g,b;
		
	}
	
	/** Attribut **/
	private Point3D position;	
	public DrawableObject3D() {}
	abstract void drawObject(GL4bcImpl env);
	
	public static class Face{
	    
	    int nbPoints;
	    ArrayList<Point3D> points;
	    Color couleur;
	    Face(ArrayList<Point3D> points,int nbp, Color couleur)
	    {
	        this.nbPoints=nbp;
	        this.couleur=couleur;
	        this.points= points;
	    }
	    Face(Point3D[] points)
	    {
	        this(points,points.length,new Color((byte)150,(byte)150,(byte)150));
	    }
	    Face(Point3D[] points,int nbp, Color couleur)
	    {
	        this.nbPoints=nbp;
	        this.couleur=couleur;
	        this.points= new ArrayList<Point3D>();
	        for(Point3D p : points)
	        {
	        	this.points.add(p);
	        }
	    }
	    Face() { 
	    	super();
	    	nbPoints=0;
	    	points = new ArrayList<Point3D>();
	    	couleur = new Color((byte)0,(byte)0,(byte)0);
	    }
	    
	}
	
	public static class FaceTexture extends Face{
		public FaceTexture() {
			super();}

		public FaceTexture(Texture t, ArrayList<Point> pointsAppliTexture,ArrayList<Point3D> points, int nbp, Color couleur) {
			super(points, nbp, couleur);
			this.pointsAppliTexture=pointsAppliTexture;
			this.t = t;
			int difference =  points.size() - pointsAppliTexture.size() ;
			for (int cpt=0; cpt<difference;cpt++)
			{
				this.pointsAppliTexture.add(new Point(0,0));
			}
		}

		public FaceTexture(Texture t,Point[] pointsAppliTexture,Point3D[] points, int nbp, Color couleur) {
			super(points, nbp, couleur);
			this.t = t;
	        this.pointsAppliTexture= new ArrayList<Point>();
	        for(Point p : pointsAppliTexture)
	        {
	        	this.pointsAppliTexture.add(p);
	        }
	        int difference =  points.length - pointsAppliTexture.length ;
				for (int cpt=0; cpt<difference;cpt++)
				{
					this.pointsAppliTexture.add(new Point(0,0));
				}
		}
		public FaceTexture(String Filename, ArrayList<Point> pointsAppliTexture,ArrayList<Point3D> points, int nbp, Color couleur) {
			super(points, nbp, couleur);
			// TODO Auto-generated constructor stub
		}


		private Texture t;
		private ArrayList<Point> pointsAppliTexture;
	}
	
	public static class Object3D extends DrawableObject3D 
	{
		String nom;
		ArrayList<Face> faces;
		
		public Object3D() {
			super();
			faces = new ArrayList<Face>();
		}

		@Override
		void drawObject(GL4bcImpl env) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public static Object3D load3DS(String filename)
	{
		
		Object3D retour = new Object3D();
		int index;
		DataInputStream file;
		byte[] input;
		int chunk_id;
		int chunk_lenght;
		char c;
		int qty, face_flag;
		ArrayList<Point3D> points = new ArrayList<Point3D>();
		ArrayList<Face> faces = new ArrayList<Face>();

		char[] octets;
		try {
			file = new DataInputStream(new FileInputStream(filename));
			input = new byte[file.available()];
			/*file.readFully(input);
			for(int cpt=0; cpt<50;cpt++)
			{
				System.out.println(Integer.toBinaryString(input[cpt]));
				System.out.println(Integer.toHexString(input[cpt]));
				System.out.println(input[cpt]);
			}*/
			int cpt=0;	
			while(file.available()>0 && cpt<20)
			{
			System.out.println(cpt);
			chunk_id = readUnsignedShort(file);
			System.out.println(Integer.toHexString(chunk_id));
			chunk_lenght = (readUnsignedInt(file));
			//if(chunk_lenght<0) chunk_lenght*=-1;
			System.out.println(Integer.toHexString(chunk_lenght));
			
			 System.out.println("bytes available :" +file.available());
			
			switch(chunk_id)
			{
			//----------------- MAIN3DS -----------------
			// Description: Main chunk, contains all the other chunks
			// Chunk ID: 4d4d 
			// Chunk Lenght: 0 + sub chunks
			//-------------------------------------------
			case 0x4d4d: 
			break;    
			//----------------- EDIT3DS -----------------
			// Description: 3D Editor chunk, objects layout info 
			// Chunk ID: 3d3d (hex)
			// Chunk Lenght: 0 + sub chunks
			//-------------------------------------------
			case 0x3d3d:
			break;
			
			//--------------- EDIT_OBJECT ---------------
			// Description: Object block, info for each object
			// Chunk ID: 4000 (hex)
			// Chunk Lenght: len(object name) + sub chunks
			//-------------------------------------------
			case 0x4000: 
				index=0;
				char[] nom =new char[20];
				do
				{
					c=(char)file.readUnsignedByte();
					nom[index]=c;
					index++;
				}while(c!= 0 && index<20);
				retour.nom = new String(nom);
				System.out.println(nom);


			break;

			//--------------- OBJ_TRIMESH ---------------
			// Description: Triangular mesh, chunks for 3d mesh info
			// Chunk ID: 4100 (hex)
			// Chunk Lenght: 0 + sub chunks
			//-------------------------------------------
			case 0x4100:
			break;
			
			//--------------- TRI_VERTEXL ---------------
			// Description: Vertices list
			// Chunk ID: 4110 (hex)
			// Chunk Lenght: 1 x unsigned short (number of vertices) 
			//             + 3 x float x (number of vertices)
			//             + sub chunks
			//-------------------------------------------
			case 0x4110:
				qty=readUnsignedShort(file);

				for(index=0;index<qty;index++)
				{
					points.add(new Point3D(file.readFloat(),file.readFloat(),file.readFloat()));
					//System.out.println(points.get(index));
				}
				break;
				
				//--------------- TRI_FACEL1 ----------------
				// Description: Polygons (faces) list
				// Chunk ID: 4120 (hex)
				// Chunk Lenght: 1 x unsigned short (number of polygons) 
				//             + 3 x unsigned short (polygon points) x (number of polygons)
				//             + sub chunks
				//-------------------------------------------
				case 0x4120:
					qty=readUnsignedShort(file);
					for(index=0;index<qty;index++)
					{
						Face f = new Face(new Point3D[]{points.get(readUnsignedShort(file)),points.get(readUnsignedShort(file)),points.get(readUnsignedShort(file))});
						face_flag=file.readUnsignedShort();
						faces.add(f);
					}
				break;
			

						
			default:
				 System.out.println(file.available());
				 file.skipBytes(chunk_lenght-6);
				 System.out.println(file.available());

				 break;
			}
			cpt++;
			}
			file.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return retour;
	}
	
	
	public static void main(String[] args)
	{
		load3DS("src/media/small_skiff.3DS");
	}
	
	public static int readUnsignedInt(DataInputStream in)
	{
		byte[] entier = new byte[4];
		try {
			in.read(entier, 0, 4);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (Byte.toUnsignedInt(entier[3]) << 24) + (Byte.toUnsignedInt(entier[2]) << 16)+(Byte.toUnsignedInt(entier[1]) << 8) +Byte.toUnsignedInt(entier[0]);
		
		
	}
	
	public static int readUnsignedShort(DataInputStream in)
	{
		byte[] entier = new byte[2];
		try {
			in.read(entier, 0, 2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return (Byte.toUnsignedInt(entier[1])<<8)+Byte.toUnsignedInt(entier[0]);
	}
	
}


