import java.util.*;
import java.io.*;

// This class implements a google-like search engine
public class searchEngine {

    public HashMap<String, LinkedList<String> > wordIndex;                  
    // this will contain a set of pairs (String, LinkedList of Strings)	
    public directedGraph internet;             // this is our internet graph
    
    
    
    // Constructor initializes everything to empty data structures 
    // It also sets the location of the internet files
    searchEngine() {
		// Below is the directory that contains all the internet files
		htmlParsing.internetFilesLocation = "internetFiles";
		wordIndex = new HashMap<String, LinkedList<String> > ();		
		internet = new directedGraph();				
    } // end of constructor2015
    
    
    // Returns a String description of a searchEngine
    public String toString () {
		return "wordIndex:\n" + wordIndex + "\ninternet:\n" + internet;
    }
    
    
    // This does a graph traversal of the internet, starting at the given url.
    // For each new vertex seen, it updates the wordIndex, the internet graph,
    // and the set of visited vertices.
    
    void traverseInternet(String url) throws Exception {
	    // breadth-first search with queue
	    Queue<String> urlQueue = new LinkedList<String>(); //store urls in urlQueue
		urlQueue.add(url); //add url to queue urlQueue
		//if site is unvisited:

		//label vertex as visited
		internet.setVisited(url,true);
		//add url to graph, each url is a vertex of graph
		internet.addVertex(url);

		while(urlQueue.peek() != null) { //while there is a url in the queue
			url = urlQueue.remove(); // store current url as var url

			// linked list called links of all outgoing links
			LinkedList<String> links = htmlParsing.getLinks(url); //add a vertex for each link as long as getVisited = false
			Iterator<String> l = links.iterator(); //iterate through each link in linked list with iterator
			while (l.hasNext() ) {
				String nextLink = l.next();
				//for debugging, ensure iterator is iterating through each link like a good iterator
				//System.out.println(nextLink);

				//add link to vertex if unvisited
				if (!internet.getVisited(nextLink)) {
					urlQueue.add(nextLink); //add link to queue to be discovered
					internet.addVertex(nextLink); //also add link to vertex of graph
					internet.setVisited(nextLink, true); // set link to visited
				}
				//make edge between current url and current link since current url linked to current link
				internet.addEdge(url,nextLink);
			}

			//build word index
			LinkedList<String> content = htmlParsing.getContent(url); //get linked list of words on current url from getContent()
			Iterator<String> c = content.iterator();
			while (c.hasNext() ) {
				String word = c.next();
				//get linked list of urls associated with the word 
				LinkedList<String> existingUrl = wordIndex.get(word);
				if (existingUrl == null) { //if there isnt a url linked to a word, make one like the strong independent code you are
					existingUrl = new LinkedList<String>();
				} 
				existingUrl.add(url); // and update existingUrl
				wordIndex.put(word,existingUrl); 
			}
		}


	/* Hints
	   0) This should take about 50-70 lines of code (or less)
	   1) To parse the content of the url, call
	   htmlParsing.getContent(url), which returns a LinkedList of Strings containing all the words at the given url. Also call htmlParsing.getLinks(url). and assign their results to a LinkedList of Strings.
	   2) To iterate over all elements of a LinkedList, use an Iterator,
	   as described in the text of the assignment
	   3) Refer to the description of the LinkedList methods at
	   http://docs.oracle.com/javase/6/docs/api/ .
	   You will most likely need to use the methods contains(String s), 
	   addLast(String s), iterator()
	   4) Refer to the description of the HashMap methods at
	   http://docs.oracle.com/javase/6/docs/api/ .
	   You will most likely need to use the methods containsKey(String s), 
	   get(String s), put(String s, LinkedList l).  
	*/
	
	
	
    } // end of traverseInternet
    
    
    /* This computes the pageRanks for every vertex in the internet graph.
       It will only be called after the internet graph has been constructed using 
       traverseInternet.
       Use the iterative procedure described in the text of the assignment to
       compute the pageRanks for every vertices in the graph. 
       
       This method will probably fit in about 30 lines.
    */
    void computePageRanks() {	
    	//iterate through all the urls using an iterator 
    	LinkedList<String> urls = internet.getVertices();
    	Iterator<String> u = urls.iterator();
    	while (u.hasNext() ) {
    		//initialize pageRank 1 for all vertices
    		//put current url into hashmap with value 1
    		internet.setPageRank(u.next(),1);
    	}    		

    	LinkedList<String> edgesInto;

		for (int i=0; i<100; i++) {
			//iterate through urls using iterator #thankful
			u = urls.iterator();
			while (u.hasNext() ) {
				String url = u.next();
				//get all the edges going into vertex, here, the vertex is the url
				edgesInto  = internet.getEdgesInto(url);
				// and iterate through all the egdges using an iterator
				Iterator<String> e = edgesInto.iterator();
				double pr = 0.5;
				//for each edge, calculate its page rank using the provided formula and sum everything up
				while(e.hasNext() ) {
					String edge = e.next();
					pr += 0.5*internet.getPageRank(edge)/internet.getOutDegree(edge);
					//for debugging purposes; make sure loop actually runs... AND IT DOES #thankfulAgain
					//System.out.println(pr);
				}	
				//set the page rank to the calculated pr
				internet.setPageRank(url,pr);
				//for debugging purposes; make sure values are being added
				//System.out.println(internet.getPageRank(url));
			}
		}
	
    } // end of computePageRanks
    
	
    /* Returns the URL of the page with the high page-rank containing the query word
       Returns the String "" if no web site contains the query.
       This method can only be called after the computePageRanks method has been executed.
       Start by obtaining the list of URLs containing the query word. Then return the URL 
       with the highest pageRank.
       This method should take about 25 lines of code.
    */
    String getBestURL(String query) {
    	query = query.toLowerCase();
		// find linked list of urls containing query
		LinkedList<String> relevantURLs = wordIndex.get(query);
		if (relevantURLs == null ) {
			return "";
		}
		//iterate through linked list of urls containing query with iterator
		Iterator<String> ru = relevantURLs.iterator();
		double maxPageRank = 0; //highest page rank, initialized to 0 for now. Won't be 0 for long
		double prURL; //variable for storing page rank of current url, compare to maxPageRank, update maxPageRank if needed
		String bestURL = "";
		while (ru.hasNext() ) {
			String rURL = ru.next();
			prURL = internet.getPageRank(rURL); //get page rank of current relevant url
			if (prURL > maxPageRank) {
				maxPageRank = prURL; //if current page rank is higher than the current maximum page rank, replace the max with the current page rank
				bestURL = rURL;
				//debugging purposes, ensure page rank is retrieved
				//System.out.println("Page rank for " + bestURL + " is " + prURL);
			}
		}
		return bestURL;
    } // end of getBestURL
    
    
	
    public static void main(String args[]) throws Exception{
    	//print out funny message to ensure code actually runs
    	//System.out.println("my ass");
		
	searchEngine mySearchEngine = new searchEngine();
	// to debug your program, start with.
	// mySearchEngine.traverseInternet("http://www.cs.mcgill.ca/~blanchem/250/a.html");
	
	// When your program is working on the small example, move on to
	mySearchEngine.traverseInternet("http://www.cs.mcgill.ca");
	
	// this is just for debugging purposes. REMOVE THIS BEFORE SUBMITTING
	//System.out.println(mySearchEngine);

	mySearchEngine.computePageRanks();
	// more debugging goodness
	//System.out.println(mySearchEngine);
	
	BufferedReader stndin = new BufferedReader(new InputStreamReader(System.in));
	String query;
	do {
	    System.out.print("Enter query: ");
	    query = stndin.readLine();
	    if ( query != null && query.length() > 0 ) {
		System.out.println("Best site = " + mySearchEngine.getBestURL(query));
	    }
	} while (query!=null && query.length()>0);				
    } // end of main :(
}
