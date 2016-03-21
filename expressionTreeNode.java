import java.lang.Math.*;

class expressionTreeNode {
    private String value;
    private expressionTreeNode leftChild, rightChild, parent;
    
    expressionTreeNode() {
        value = null; 
        leftChild = rightChild = parent = null;
    }
    
    // Constructor
    /* Arguments: String s: Value to be stored in the node
                  expressionTreeNode l, r, p: the left child, right child, and parent of the node to created      
       Returns: the newly created expressionTreeNode               
    */
    public expressionTreeNode(String s, expressionTreeNode l, expressionTreeNode r, expressionTreeNode p) {
        value = s; 
        leftChild = l; 
        rightChild = r;
        parent = p;
    }
    
    /* Basic access methods */
    String getValue() { return value; }

    expressionTreeNode getLeftChild() { return leftChild; }

    expressionTreeNode getRightChild() { return rightChild; }

    expressionTreeNode getParent() { return parent; }


    /* Basic setting methods */ 
    void setValue(String o) { value = o; }
    
    // sets the left child of this node to n
    void setLeftChild(expressionTreeNode n) { 
        leftChild = n; 
        n.parent = this; 
    }
    
    // sets the right child of this node to n
    void setRightChild(expressionTreeNode n) { 
        rightChild = n; 
        n.parent=this; 
    }
    

    // Returns the root of the tree describing the expression s
    // Watch out: it makes no validity checks whatsoever!
    public expressionTreeNode(String s) {
        // check if s contains parentheses. If it doesn't, then it's a leaf
        if (s.indexOf("(")==-1) setValue(s);
        else {  // it's not a leaf

            /* break the string into three parts: the operator, the left operand,
               and the right operand. ***/
            setValue( s.substring( 0 , s.indexOf( "(" ) ) );
            // delimit the left operand 2008
            int left = s.indexOf("(")+1;
            int i = left;
            int parCount = 0;
            // find the comma separating the two operands
            while (parCount>=0 && !(s.charAt(i)==',' && parCount==0)) {
                if ( s.charAt(i) == '(' ) parCount++;
                if ( s.charAt(i) == ')' ) parCount--;
                i++;
            }
            int mid=i;
            if (parCount<0) mid--;

        // recursively build the left subtree
            setLeftChild(new expressionTreeNode(s.substring(left,mid)));
    
            if (parCount==0) {
                // it is a binary operator
                // find the end of the second operand.F13
                while ( ! (s.charAt(i) == ')' && parCount == 0 ) )  {
                    if ( s.charAt(i) == '(' ) parCount++;
                    if ( s.charAt(i) == ')' ) parCount--;
                    i++;
                }
                int right=i;
                setRightChild( new expressionTreeNode( s.substring( mid + 1, right)));
        }
    }
    }


    // Returns a copy of the subtree rooted at this node... 2014
    expressionTreeNode deepCopy() {
        expressionTreeNode n = new expressionTreeNode();
        n.setValue( getValue() );
        if ( getLeftChild()!=null ) n.setLeftChild( getLeftChild().deepCopy() );
        if ( getRightChild()!=null ) n.setRightChild( getRightChild().deepCopy() );
        return n;
    }
    
    // Returns a String describing the subtree rooted at a certain node.
    public String toString() {
        String ret = value;
        if ( getLeftChild() == null ) return ret;
        else ret = ret + "(" + getLeftChild().toString();
        if ( getRightChild() == null ) return ret + ")";
        else ret = ret + "," + getRightChild().toString();
        ret = ret + ")";
        return ret;
    } 
    
    //takes string and convert it to actual operation
    static public double calculate(String operation, double x1, double x2) {
    	 if (operation.equals("add")) return x1+x2;
    	 if (operation.equals("mult")) return x1*x2;
    	 if (operation.equals("minus")) return x1-x2;
    	 if (operation.equals("sin")) return Math.sin(x1);
    	 if (operation.equals("cos")) return Math.cos(x1);
    	 if (operation.equals("exp")) return Math.exp(x1);
    	 else return 0; 
    	 
    }


    // Returns the value of the expression rooted at a given node
    // when x has a certain value
    double evaluate(double x) {
    	if (getLeftChild()==null) {
    		// leaf
    		if (getValue().equals("x")) return x;
    		else return Double.parseDouble(getValue());
    		}
            //else not a leaf, get left and right children
            //and evaluate them
    	else {
    		if (getRightChild()!=null) {
    			return calculate(getValue(),
    					getLeftChild().evaluate(x),
    					getRightChild().evaluate(x));
    			}
    		else {
    			return calculate(getValue(),
    					getLeftChild().evaluate(x),
    					0);
    			}
    		}
    	}                                                 

    /* returns the root of a new expression tree representing the derivative of the
       original expression */
    expressionTreeNode differentiate() {
    	if (getLeftChild()==null) {
    		//leaf
    		if (getValue().equals("x")) return new expressionTreeNode("1");
    		else return new expressionTreeNode("0");
    		}
        //not leaf
    	if (getValue().equals("add") || getValue().equals("minus")) {
    		return new expressionTreeNode(getValue(),
    				getLeftChild().differentiate(),
    				getRightChild().differentiate(),
    				getParent());
    		}
    	if (getValue().equals("mult")) {
    		 // make 3 nodes: "add" & 2 children "mult"
    		 expressionTreeNode l;
    		 l = new expressionTreeNode("mult", getLeftChild().differentiate(),
    				 getRightChild().deepCopy(),
    				 getParent());

             expressionTreeNode r;
    		 r = new expressionTreeNode("mult", getLeftChild().deepCopy(),
    				 getRightChild().differentiate(),
    				 getParent());
    		 return new expressionTreeNode("add", l, r, getParent());
    		 }
    	if (getValue().equals("sin")) {
    		 // make 3 nodes: "mult" & 2 children "cos" and diff.
    		 expressionTreeNode l;
             expressionTreeNode r;
             expressionTreeNode p;
    		 l=new expressionTreeNode("cos", getLeftChild().deepCopy(),null, getParent());
    		 r=getLeftChild().differentiate();
    		 p=getParent();
    		 return new expressionTreeNode("mult",l,r,p);
    		 }
    	if (getValue().equals("cos")) {
    		 // make 3 nodes: "minus" & children 0 and ("mult" with two children "sin" and diff).
    		 expressionTreeNode r;
    		 r=new expressionTreeNode("mult", new expressionTreeNode("sin", getLeftChild().deepCopy(), null, getParent()),
    				 getLeftChild().differentiate(), getParent());
    		 return new expressionTreeNode("minus", new expressionTreeNode("0",null,null, getParent()), r, getParent());
    		 }
    	if (getValue().equals("exp")) {
    			 return new expressionTereNode("mult", deepCopy(), getLeftChild().differentiate(), getParent());
    			 } 
    	return null;
    }
        
    
    public static void main(String args[]) {
       // expressionTreeNode e = new expressionTreeNode("mult(add(2,x),cos(x))");
        expressionTreeNode e = new expressionTreeNode("mult(x,add(add(2,x),cos(minus(x,4))))");
       // expressionTreeNode e = new expressionTreeNode("mult(add(2,x),cos(x))");
        System.out.println(e);
        System.out.println(e.evaluate(1));
        System.out.println(e.differentiate());
   
 }
}