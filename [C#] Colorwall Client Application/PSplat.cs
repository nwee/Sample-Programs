using UnityEngine;
using System.Collections;
using System.IO;
using SocketIO;
using System.Collections.Generic;

//ws://192.168.1.69:3000/socket.io/?EIO=3&transport=websocket
public class PSplat : MonoBehaviour 
{
	//prefabs
	public GameObject explosion1;		
	public GameObject explosionTextures1;
	public GameObject explosion2;		
	public GameObject explosionTextures2;
	public GameObject explosion3;	
	public GameObject explosionTextures3;
	public GameObject explosion4;	
	public GameObject explosionTextures4;
	public GameObject explosion5;	
	public GameObject explosionTextures5;
	public GameObject explosion6;	
	public GameObject explosionTextures6;
	public GameObject msgContainer;

	//Game Variables
	private SocketIOComponent socket;
	private GameObject[] explosion = new GameObject[6];
	private GameObject[] explosionTextures = new GameObject[6];
	private float splatDelay = 30f;
	private int splatIndex = 0, BG_Age = 1, baseLayer = 0, textLayer = 1, msgLayer = 2;
	private double timer = 0.0;

	//Socket-changing Variables
	private Vector3 paintCoord;
	private float rcvX , rcvY;
	private string rcvText, rcvTimestamp = "SPONSORED", sponID = "0";
	private Color paintColour = Color.white;
	private Dictionary<string,string> data = new Dictionary<string, string>();

	void Start() {
		Screen.SetResolution (288, 162, true);

		//initialise all the receiving functions
		socket = GameObject.Find("SocketIO").GetComponent<SocketIOComponent>();
		socket.On("open", OnSocketOpen);
		socket.On ("new message", getMsg);
		socket.On ("fake hit", fakeHit);
		socket.On ("new backgrounds", nextBG);
		socket.On ("clear screen", clearScrn);
		socket.On ("close", OnSocketClose);

		explosion[0] = explosion1;
		explosion[1] = explosion2;
		explosion[2] = explosion3;
		explosion[3] = explosion4;
		explosion[4] = explosion5;
		explosion[5] = explosion6;

		explosionTextures[0] = explosionTextures1;
		explosionTextures[1] = explosionTextures2;
		explosionTextures[2] = explosionTextures3;
		explosionTextures[3] = explosionTextures4;
		explosionTextures[4] = explosionTextures5;
		explosionTextures [5] = explosionTextures6;
	
	}

	/*------------------------------------------------------
	 * 					SOCKET EVENTS
	------------------------------------------------------*/
	public void OnSocketOpen(SocketIOEvent ev){
		Debug.Log("[SocketIO] Open received: " + ev.name + " " + ev.data);
		socket.Emit ("connect game");
	}
	public void getMsg(SocketIOEvent ev){
		Debug.Log("[SocketIO] getMsg received: " + ev.name + " " + ev.data);
		rcvText = wrapString (ev.data["message"].ToString().Replace ("\"", ""), 12);
		rcvTimestamp = ev.data["timestamp"].ToString().Replace ("\"","");

		float r = float.Parse(ev.data ["color"] ["r"].ToString())/255f;
		float g = float.Parse(ev.data ["color"] ["g"].ToString())/255f;
		float b = float.Parse(ev.data ["color"] ["b"].ToString())/255f;
		paintColour = new Color(r,g,b);

		data ["timestamp"] = rcvTimestamp;

		paintCoord.x = rcvX;
		paintCoord.y = rcvY;

		if (data ["timestamp"].Equals ("SPONSORED")) sponID = "ad"+ev.data ["mid"].ToString ();
		paintSplat();
	}
	public void fakeHit(SocketIOEvent ev) {	
		Debug.Log("[SocketIO] fakeHit received: " + ev.name + " " + ev.data);
		rcvText = wrapString (ev.data["message"].ToString().Replace ("\"", ""), 12);
		rcvTimestamp = ev.data["timestamp"].ToString().Replace ("\"","");

		float r = float.Parse(ev.data ["color"] ["r"].ToString())/255f;
		float g = float.Parse(ev.data ["color"] ["g"].ToString())/255f;
		float b = float.Parse(ev.data ["color"] ["b"].ToString())/255f;
		paintColour = new Color(r,g,b);
		 
		data ["timestamp"] = rcvTimestamp;

		rcvX = float.Parse(ev.data ["x"].ToString ())/100;
		rcvY = 1f - float.Parse (ev.data ["y"].ToString ()) / 100;

		paintCoord.x = (float)Camera.main.ScreenToWorldPoint(new Vector2 (rcvX*Screen.width, rcvX*Screen.height)).x;
		paintCoord.y = (float)Camera.main.ScreenToWorldPoint(new Vector2 (rcvY*Screen.width, rcvY*Screen.height)).y;

		if (data ["timestamp"].Equals ("SPONSORED")) sponID = "ad"+ev.data ["mid"].ToString ();
		paintSplat();
	}
	public void clearScrn(SocketIOEvent ev) {
		Debug.Log("[SocketIO] clearScrn received: " + ev.name + " " + ev.data);
		clearPaint();
	}
	public void nextBG(SocketIOEvent ev) {
		Debug.Log("[SocketIO] nextBG received: LEN:"+ev.data["length"].ToString()+" "+ ev.name + " " + ev.data);
		int max = 0;
		int randIndex = Random.Range(0,int.Parse(ev.data["length"].ToString())-1);

		int[] used = new int[4];

		if (int.Parse(ev.data["length"].ToString()) >= 4) {
			max = 4;
		}
		for (int i = 0; i < max; i++) {
			randIndex = Random.Range(0,int.Parse(ev.data["length"].ToString())-1);
			//checks all used if it exists
			bool alrUsed = true;
			while (alrUsed) {
				alrUsed = false; //assumes its unique
				for (int x = 0; x < used.Length; x++) {
					if (used[x] == randIndex) { //if exists
						alrUsed = true;
					}
				}
				if (alrUsed) { //if its not unique rerandom and 
					randIndex = Random.Range(0,int.Parse(ev.data["length"].ToString())-1);
				}
				else { 
					used[i] = randIndex; //if its unique save the number into the array
				}
			}
		
			string url = ev.data["backgrounds"][randIndex].ToString().Replace ("\"","");
			StartCoroutine(loadNext(url,chooseBG(), false));
		}
	}
	public void OnSocketClose(SocketIOEvent ev) {	
		Debug.Log("[SocketIO] Close received: " + ev.name + " " + ev.data);
	}	

	/*------------------------------------------------------
	 * 					MAIN FUNCTIONS
	------------------------------------------------------*/
	void Update () 	{
		if (splatIndex > 30) { //reset after 30
			splatIndex = 0;
		}
		timer += Time.deltaTime;
		if (timer >= 20d) {
			timer = 0.00;
			sponCheck();
		}

		//trigger for paintSplat event
		if (Input.GetButtonDown("Fire1")) {
			socket.Emit ("get latest message", new JSONObject(data));
			Debug.Log("[SocketIO] 'get latest message' sent");
			rcvX = Input.mousePosition.x/Screen.width;
			rcvY = Input.mousePosition.y/Screen.height;
		}
		else if (Input.GetKeyDown(KeyCode.Escape)) {
			Application.Quit();
		}
		else if (Input.GetKeyDown("`")) {
			clearPaint();
		}
		else if (Input.GetKeyDown("3")) {
			sponCheck();
		}
	}

	/*
	 * Instantiates 1 of the 6 paint splatters associated 
	 * texture at a given coordinate. The alpha layer is 
	 * given a colour before the texture layer is applied.
	 */
	void paintSplat () {
		string name = "splat"+splatIndex;

		isClear ();
		borderCheck ();

		int rand = Random.Range(0,5);
		paintCoord.z = -splatIndex;

		GameObject paintTextures = Instantiate(explosionTextures[rand], paintCoord , Quaternion.identity) as GameObject;
		paintTextures.transform.localScale = new Vector3(24,24,0);
		paintTextures.gameObject.tag = "paintSplat";
		paintTextures.gameObject.name = name;
		paintTextures.renderer.sortingOrder = textLayer;

		explosion[rand].GetComponent<SpriteRenderer>().color = paintColour;
		GameObject paintAlpha = Instantiate(explosion[rand], paintCoord , Quaternion.identity) as GameObject;
		paintAlpha.transform.localScale = new Vector3(24,24,0);
		paintAlpha.gameObject.name = name;
		paintAlpha.gameObject.tag = "paintSplat";
		paintAlpha.renderer.sortingOrder = baseLayer;

		baseLayer += 3;
		textLayer += 3;
		StartCoroutine (msgDisplay(name,rcvTimestamp.Equals("SPONSORED")));
		Destroy(paintTextures,splatDelay);
		Destroy(paintAlpha,splatDelay);

		socket.Emit ("fired", new JSONObject(data));
		Debug.Log("[SocketIO] 'fired' sent, timestamp: "+data["timestamp"]);
		splatIndex++;
	}

	/*
	 * Displays the message after a certain delay and
	 * removes the objects before the animation ends.
	 */
	IEnumerator msgDisplay(string name, bool sponsored) {
		yield return new WaitForSeconds(.3f);
		GameObject msgAnchor;
		if (sponsored) {
			msgAnchor = new GameObject(name);
			msgAnchor.AddComponent<SpriteRenderer>();
			msgAnchor.GetComponent<SpriteRenderer>().sprite = Resources.Load(sponID,typeof(Sprite)) as Sprite;
			paintCoord.y += 1;
			msgAnchor.transform.position = paintCoord;
			msgAnchor.transform.localScale = new Vector3(2,2,0);
			msgAnchor.tag = "msgAnchor";
		}
		else {
			paintCoord.y += 1;
			msgAnchor = Instantiate (msgContainer, paintCoord, Quaternion.identity) as GameObject;
			msgAnchor.GetComponent<TextMesh> ().text = rcvText;
			msgAnchor.name = name;
			msgAnchor.tag = "msgAnchor";
		}
		msgAnchor.renderer.sortingOrder = msgLayer;
		msgLayer += 3;
		Destroy(msgAnchor, splatDelay-.5f);
	}

	/*
	 * Finds all objects with the tags and removes them
	 */
	void clearPaint() {
		Object[] allObjects = FindObjectsOfType(typeof(GameObject));
		foreach(GameObject obj in allObjects) {
			if(obj.tag == "msgAnchor" ||obj.tag == "paintSplat"){
				Destroy(obj);
			}
		}
	}
	
	/* 
	 * Selects randomly from the oldest BG objects. 
	 * Finds all the BG objects with the youngest age and  
	 * stores the into an array. Random index of the array
	 * chosen with the object to return.
	 * NOTE *
	 * guiText.text is used to store the image's age and is 
	 * used to compare age by int.Parsing the string.
	 */
	string chooseBG() { 
		string[] set = new string[6];
		int setIndex = 0;
		bool found = false;
		while (!found){ 
			Object[] allObjects = FindObjectsOfType(typeof(GameObject));
			foreach(GameObject obj in allObjects) { 
				if(obj.tag == "BG" && int.Parse(obj.guiText.text) == BG_Age){ 
					set[setIndex] = obj.name;
					setIndex++;
					found = true;
				}
			}
			if (!found) {
				BG_Age++;
			}
		}
		string retVal = set [Random.Range (0, setIndex)];
		GameObject.Find (retVal).guiText.text = (BG_Age+1).ToString();
		return retVal;
	}

	/*
	 * Loads the next image to replace an older one.
	 * Replaces image with a slow fade in of the new
	 * image, with a fade out of the old one.
	 */
	IEnumerator loadNext(string url, string BG, bool sponBG) {
		float opacity = 0;
		GameObject temp = new GameObject("temp");
		temp.AddComponent<GUITexture>();
		temp.transform.localScale = GameObject.Find(BG).transform.localScale;
		temp.transform.position = new Vector3(GameObject.Find(BG).transform.position.x, GameObject.Find(BG).transform.position.y, 1);
		temp.layer = 8;
		if (!sponBG) {
			GameObject.Find(BG).guiText.fontSize = 0;
			WWW www = new WWW("file://"+url);
			yield return www;
			temp.guiTexture.texture = www.texture;
		}
		else {
			int i = Random.Range(1,4);
			Object[] allObjects = FindObjectsOfType(typeof(GameObject));
			foreach(GameObject obj in allObjects) {
				if(obj.tag == "BG" && obj.guiText.fontSize == 1) { 
					if (obj.guiTexture.texture.name.Equals("spon"+i.ToString())) {
						print ("Existing is "+obj.guiTexture.texture.name);
						while (obj.guiTexture.texture.name.Equals("spon"+i.ToString())) {
							i = Random.Range (1,4);
						}
					} 
				}
			}
			GameObject.Find(BG).guiText.fontSize = 1;
			temp.guiTexture.texture = Resources.Load("spon"+i.ToString()) as Texture;
		}

		temp.guiTexture.color = new Color (150 / 255f, 150 / 255f, 150 / 255f, opacity);

		for (int i = 0; i<64; i++) {
			yield return new WaitForSeconds (0.05f);	
			if (opacity >= 128f) {
				opacity = 128f;
			} else opacity += 2/255f;
			temp.guiTexture.color = new Color (150 / 255f, 150 / 255f, 150 / 255f, opacity);  
			GameObject.Find(BG).guiTexture.color = new Color (150 / 255f, 150 / 255f, 150 / 255f, 128f-opacity);  
		}

		GameObject.Find(BG).guiTexture.texture = temp.guiTexture.texture;
		GameObject.Find(BG).guiTexture.color = new Color (150 / 255f, 150 / 255f, 150 / 255f, 128f);  
		Destroy (temp);
	}

	/*
	 * Loads 2 different sponsored images 
	 */
	IEnumerator sponsoredBGs(string BG, string BG2) {
		float opacity = 0;

		GameObject.Find (BG).guiText.fontSize = 1;
		GameObject.Find (BG2).guiText.fontSize = 1;

		GameObject temp = new GameObject("temp");
		temp.AddComponent<GUITexture>();
		temp.transform.localScale = GameObject.Find(BG).transform.localScale;
		temp.transform.position = new Vector3(GameObject.Find(BG).transform.position.x, GameObject.Find(BG).transform.position.y, 1);
		temp.layer = 8;
		
		GameObject temp2 = new GameObject("temp2");
		temp2.AddComponent<GUITexture>();
		temp2.transform.localScale = GameObject.Find(BG2).transform.localScale;
		temp2.transform.position = new Vector3(GameObject.Find(BG2).transform.position.x, GameObject.Find(BG2).transform.position.y, 1);
		temp2.layer = 8;
		
		int index1 = Random.Range(1,4);
		int index2 = Random.Range(1,4);
		
		if (index2 == index1) {
			while (index2==index1) {
				index2 = Random.Range(1,4);
			}
		}
		
		temp.guiTexture.texture = Resources.Load("spon"+index1.ToString()) as Texture;
		temp2.guiTexture.texture = Resources.Load("spon"+index2.ToString()) as Texture;
		
		temp.guiTexture.color = new Color (150 / 255f, 150 / 255f, 150 / 255f, opacity);
		temp2.guiTexture.color = new Color (150 / 255f, 150 / 255f, 150 / 255f, opacity);
		
		for (int i = 0; i<64; i++) {
			yield return new WaitForSeconds (0.05f);	
			if (opacity >= 128f) {
				opacity = 128f;
			} else opacity += 2/255f;
			temp.guiTexture.color = new Color (150 / 255f, 150 / 255f, 150 / 255f, opacity);  
			temp2.guiTexture.color = new Color (150 / 255f, 150 / 255f, 150 / 255f, opacity);  
			GameObject.Find(BG).guiTexture.color = new Color (150 / 255f, 150 / 255f, 150 / 255f, 128f-opacity);  
			GameObject.Find(BG2).guiTexture.color = new Color (150 / 255f, 150 / 255f, 150 / 255f, 128f-opacity);  
		}
		
		GameObject.Find(BG).guiTexture.texture = temp.guiTexture.texture;
		GameObject.Find(BG).guiTexture.color = new Color (150 / 255f, 150 / 255f, 150 / 255f, 128f);  
		
		GameObject.Find(BG2).guiTexture.texture = temp2.guiTexture.texture;
		GameObject.Find(BG2).guiTexture.color = new Color (150 / 255f, 150 / 255f, 150 / 255f, 128f);  
		Destroy (temp);
		Destroy (temp2);
	}

	/*------------------------------------------------------
	 * 					HELPER FUNCTIONS
	------------------------------------------------------*/
	/* 
	 * Checks if there are sponsored images in the background.
	 * Will load sponsored images if there are none.
	 * NOTE * 
	 * guiText.fontSize is used as de facto boolean to indicate if 
	 * it is a sponsored BG: 0 is no, and 1 is yes. 
	 */
	void sponCheck() {
		int numSpon = 0;
		string sName = "";
		Object[] allObjects = FindObjectsOfType(typeof(GameObject));
		foreach(GameObject obj in allObjects) {
			if(obj.tag == "BG" && obj.guiTexture.texture.name.Contains("spon")) {
				obj.guiText.fontSize = 1;
				numSpon++;
				if (numSpon == 1) {
					sName = obj.name;
				}
			}
			else if (obj.tag == "BG") obj.guiText.fontSize = 0;
		}
		
		if (numSpon == 0) {
			StartCoroutine(sponsoredBGs (chooseBG(),chooseBG()));
		}
		else if (numSpon == 1) {
			//increases age so new one doesnt coincide with existing one
			GameObject.Find (sName).guiText.text = (BG_Age+2).ToString(); 
			string chosen = chooseBG();
			while (chosen.Equals(sName)) {
				chosen = chooseBG();
			}
			StartCoroutine(loadNext("",chosen, true)); 
			GameObject.Find (sName).guiText.text = (BG_Age+1).ToString();
		}
		else if (numSpon >= 2) {
			foreach(GameObject obj in allObjects) {
				if(obj.tag == "BG" && obj.guiTexture.texture.name.Contains("spon")) {
					obj.guiText.text = (BG_Age+1).ToString();
				}
			}
		}
	}

	/*
	 * Checks the screen if its clear. If it is then
	 * reset the layers.
	 */
	void isClear() {
		bool clear = true;
		Object[] allObjects = FindObjectsOfType(typeof(GameObject));
		foreach(GameObject obj in allObjects) {
			if(obj.tag == "msgAnchor" ||obj.tag == "paintSplat"){
				clear = false;
				break;
			}
		}
		if (clear) {
			splatIndex = 0;
			baseLayer = 0;
			textLayer = 1;
			msgLayer = 2;
		}
	}

	/*
	 * Checks and corrects the coordinates so
	 * the message doesn't appear outside the window
	 */
	void borderCheck() {
		if (.85f < rcvX) rcvX = .85f;
		else if (rcvX < .15f) rcvX = .15f;

		if (rcvY > 0.55f) rcvY = 0.55f;
		else if (rcvY < 0.30f)	rcvY = 0.30f;

		paintCoord.x = (float)Camera.main.ScreenToWorldPoint(new Vector2 (rcvX*Screen.width, rcvY*Screen.height)).x;
		paintCoord.y = (float)Camera.main.ScreenToWorldPoint(new Vector2 (rcvX*Screen.width, rcvY*Screen.height)).y;
	}

	/*
	 * Wraps the string using the given character 
	 * width by adding \n to the newlines.
	 */
	string wrapString(string msg, int width) {
		string[] words = msg.Split (" " [0]);
		string retVal = ""; //returning string 
		string NLstr = "";  //leftover string on new line
		
		for (int index = 0 ; index < words.Length ; index++ ) {
			words[index].Trim();
			//if word exceeds width
			if (words[index].Length >= width+2) {
				string[] temp = new string[5];
				int i = 0;
				while (words[index].Length > width) { //word exceeds width, cut it at widrh
					temp[i] = words[index].Substring(0,width) +"\n"; //cut the word at width
					words[index] = words[index].Substring(width); 	//keep remaining word
					i++;
					if (words[index].Length <= width) { //the balance is smaller than width
						temp[i] = words[index];
						NLstr = temp[i];
					}
				}
				retVal += "\n";
				for (int x = 0 ; x < i+1 ; x++) { //loops through temp array
					retVal = retVal+temp[x];
				}
			}
			else if (index == 0) {
				retVal = words[0];
				NLstr = retVal;
			}
			else if (index > 0) {
				if (NLstr.Length + words[index].Length <= width ) {
					retVal = retVal+" "+words[index];
					NLstr = NLstr+" "+words[index]; //add the current line length
				}
				else if (NLstr.Length + words[index].Length > width) {
					retVal = retVal+ "\n" + words[index];
					NLstr = words[index]; //reset the line length
				}
			}
		}
		return retVal;
	}
}