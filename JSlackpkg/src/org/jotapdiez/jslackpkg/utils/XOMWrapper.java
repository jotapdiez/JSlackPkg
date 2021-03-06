package org.jotapdiez.jslackpkg.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import nu.xom.*;

public class XOMWrapper
{
	String default_xmlns = "";
	
	public static Document createDocument(String rootName, String nameSpace)
	{
		Element root = new Element(rootName, nameSpace); 
		Document doc = new Document(root);
		return doc;
	}
	
	public static void toFile(Document doc, String filePath)
	{
		toFile(doc, new File(filePath));
	}
	
	public static void toFile(Document doc, File file)
	{
		try
		{
			OutputStream stream = new FileOutputStream(file);
			Serializer serializer = new Serializer(stream);
	        try
			{
				serializer.write(doc);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1)
		{
			e1.printStackTrace();
		}
    }
	
	public static Document fromFile(String filePath)
	{
		return fromFile(new File(filePath));
	}
	
	public static Document fromFile(File file)
	{
		Builder builder = new Builder();
		try
		{
			return builder.build(file);
		} catch (ValidityException e)
		{
			e.printStackTrace();
		} catch (ParsingException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static Document fromString(String xml)
	{
		Builder builder = new Builder();
		try
		{
//			Document doc = new Document(generated);
			
			return builder.build(xml, "http://jasperreports.sourceforge.net/jasperreports");
		} catch (ValidityException e)
		{
			e.printStackTrace();
		} catch (ParsingException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static String toString(Element el)
	{
		return el.toXML();
	}
	
	public static String toString(Document doc)
	{
		return doc.toXML();
	}

	/*
	 * XOMWrapper
	 */
	
	/**Gets the next sibling of a given node.
	*
	* @param node The reference node.
	* @return The next Sibling, or null.
	*/
	public static Node getNextSibling(Node node) {
		ParentNode parent = node.getParent();
		int i = parent.indexOf(node);
		if (i+1 >= parent.getChildCount()) return null;
		return parent.getChild(i+1);
	}

	/**Gets the first next sibling of a given node whose tagname matches the given string.
	*
	* @param current The reference node.
	* @param tagName The tagname of a node to look for
	* @return The matched next Sibling, or null.
	*/
	public static Node getNextSibling(Node current, String tagName) {
		Element matchedElement =null;
		while (true) {
			Element next = (Element) getNextSibling(current);
			if (next != null) {
				if (next.getLocalName().equals(tagName)){
					matchedElement=next;
					break;
				}
				else{
					current = next;
				}
			} else {
				break;
			}
		}
		return matchedElement;
	}


	/**Gets the previous sibling of a given node.
	*
	* @param node The reference node.
	* @return The previous Sibling, or null.
	*/
	public static Node getPreviousSibling(Node node) {
		ParentNode parent = node.getParent();
		int i = parent.indexOf(node);
		if (i==0) return null;
		return parent.getChild(i-1);
	}


	/**Gets the first previous sibling of a given node whose tagname matches the given string.
	*
	* @param current The reference node.
	* @param tagName The tagname of a node to look for
	* @return The matched previous Sibling, or null.
	*/
	public static Node getPreviousSibling(Node current, String tagName) {
		Element matchedElement =null;
		while (true) {
			Element prev = (Element) getPreviousSibling(current);
			if (prev != null) {
				if (prev.getLocalName().equals(tagName)){
					matchedElement=prev;
					break;
				}
				else{
					current = prev;
				}
			} else {
				break;
			}
		}
		return matchedElement;
	}

	/**Inserts a node so that it occurs before a reference node. The new node
	 * must not currently have a parent.
	 *
	 * @param node The reference node.
	 * @param newNode The new node to insert.
	 */
	public static void insertBefore(Node node, Node newNode) {
		ParentNode parent = node.getParent();
		int i = parent.indexOf(node);
		parent.insertChild(newNode, i);
	}

	/**Inserts a node so that it occurs after a reference node. The new node
	 * must not currently have a parent.
	 *
	 * @param node The reference node.
	 * @param newNode The new node to insert.
	 */
	public static void insertAfter(Node node, Node newNode) {
		ParentNode parent = node.getParent();
		int i = parent.indexOf(node);
		parent.insertChild(newNode, i+1);
	}

//	/**
//	 * Gets the next node. This element need not be a sibling
//	 * @param node: starting node
//	 * @return
//	 */
//	public static Node getNext(Node node) {
//		Element parent = (Element) node.getParent();
//		if (parent == null || parent.getLocalName().equals(XmlDeclarations.MOLECULE_EL)){
//			return null;
//		}
//		int index = parent.indexOf(node);
//		if (index +1 >=parent.getChildCount()) return getNext(parent);//reached end of element
//		Element next =(Element) parent.getChild(index+1);
//		Elements children =next.getChildElements();
//		while (children.size()!=0){
//			next =children.get(0);
//			children =next.getChildElements();
//		}
//		return next;
//	}
//
//	/**
//	 * Gets the previous node. This element need not be a sibling
//	 * @param node: starting node
//	 * @return
//	 */
//	public static Node getPrevious(Node node) {
//		Element parent = (Element) node.getParent();
//		if (parent == null || parent.getLocalName().equals(XmlDeclarations.MOLECULE_EL)){
//			return null;
//		}
//		int index = parent.indexOf(node);
//		if (index ==0) return getPrevious(parent);//reached beginning of element
//		Element previous =(Element) parent.getChild(index-1);
//		Elements children =previous.getChildElements();
//		while (children.size()!=0){
//			previous =children.get(children.size()-1);
//			children =previous.getChildElements();
//		}
//		return previous;
//	}

	/**
	 * Sets the first text child of the group to the newName
	 * Throws an exception if the first child is not a Text node
	 * @param group
	 * @param newName
	 */
	public static void setTextChild(Element group, String newName){
		Node textNode =group.getChild(0);
		if (textNode instanceof Text){
			((Text)textNode).setValue(newName);
		}
		else{
			throw new IllegalArgumentException("No Text Child Found!");
		}
	}

	/**
	 * Returns an LinkedList containing sibling elements of the given type after the given element.
	 * These elements need not be continuous
	 * @param currentElem: the element to look for following siblings of
	 * @param type: the "localname" of the element type desired
	 * @return
	 */
	public static List<Element> getNextSiblingsOfType(Element currentElem, String type) {
		List<Element> laterSiblingElementsOfType= new LinkedList<Element>();
		Element parent =(Element) currentElem.getParent();
		if (parent==null){
			return laterSiblingElementsOfType;
		}
		Elements potentialMatches =parent.getChildElements(type);
		int indexOfCurrentElem =parent.indexOf(currentElem);
		for (int i = 0; i < potentialMatches.size(); i++) {
			if (parent.indexOf(potentialMatches.get(i)) > indexOfCurrentElem){
				laterSiblingElementsOfType.add(potentialMatches.get(i));
			}
		}
		return laterSiblingElementsOfType;
	}

	/**
	 * Returns an LinkedList containing sibling elements of the given type after the given element.
	 * @param currentElem: the element to look for following siblings of
	 * @param type: the "localname" of the element type desired
	 * @return
	 */
	public static List<Element> getNextAdjacentSiblingsOfType(Element currentElem, String type) {
		List<Element> siblingElementsOfType= new LinkedList<Element>();
		Element parent =(Element) currentElem.getParent();
		if (parent==null){
			return siblingElementsOfType;
		}
		Element nextSibling = (Element) XOMWrapper.getNextSibling(currentElem);
		while (nextSibling !=null && nextSibling.getLocalName().equals(type)){
			siblingElementsOfType.add(nextSibling);
			nextSibling = (Element) XOMWrapper.getNextSibling(nextSibling);
		}

		return siblingElementsOfType;
	}

	/**
	 * Returns an LinkedList containing sibling elements of the given types after the given element.
	 * These elements need not be continuous and are returned in the order encountered
	 * @param currentElem: the element to look for following siblings of
	 * @param types: An array of the "localname"s of the element types desired
	 * @return
	 */
	public static List<Element> getNextSiblingsOfTypes(Element currentElem, String[] types){
		List<Element> laterSiblingElementsOfTypes= new LinkedList<Element>();
		currentElem =(Element) getNextSibling(currentElem);
		while (currentElem !=null){
			String name =currentElem.getLocalName();
			for (String type : types) {
				if (name.equals(type)){
					laterSiblingElementsOfTypes.add(currentElem);
					break;
				}
			}
			currentElem =(Element) getNextSibling(currentElem);
		}
		return laterSiblingElementsOfTypes;
	}

	/**
	 * Returns an LinkedList containing sibling elements of the given type before the given element.
	 * These elements need not be continuous
	 * @param currentElem: the element to look for previous siblings of
	 * @param type: the "localname" of the element type desired
	 * @return
	 */
	public static List<Element> getPreviousSiblingsOfType(Element currentElem, String type) {
		List<Element> earlierSiblingElementsOfType= new LinkedList<Element>();
		Element parent =(Element) currentElem.getParent();
		if (parent==null){
			return earlierSiblingElementsOfType;
		}
		Elements potentialMatches =parent.getChildElements(type);
		int indexOfCurrentElem =parent.indexOf(currentElem);
		for (int i = 0; i < potentialMatches.size(); i++) {
			if (parent.indexOf(potentialMatches.get(i)) < indexOfCurrentElem){
				earlierSiblingElementsOfType.add(potentialMatches.get(i));
			}
		}
		return earlierSiblingElementsOfType;
	}

	/**
	 * Gets the next sibling element of the given element. If this element's name is within the elementsToIgnore array this is repeated
	 * If no appropriate element can be found null is returned
	 * @param startingEl
	 * @param elementsToIgnore
	 * @return
	 */
	public static Element getNextSiblingIgnoringCertainElements(Element startingEl, String[] elementsToIgnore){
		ParentNode parent = startingEl.getParent();
		if (parent==null){
			return null;
		}
		int i = parent.indexOf(startingEl);
		if (i+1 >= parent.getChildCount()) return null;
		Element next =(Element)parent.getChild(i+1);
		String elName =next.getLocalName();
		for (String namesToIgnore : elementsToIgnore) {
			if (elName.equals(namesToIgnore)){
				return getNextSiblingIgnoringCertainElements(next, elementsToIgnore);
			}
		}
		return next;
	}


	/**
	 * Gets the previous sibling element of the given element. If this element's name is within the elementsToIgnore array this is repeated
	 * If no appropriate element can be found null is returned
	 * @param startingEl
	 * @param elementsToIgnore
	 * @return
	 */
	public static Element getPreviousSiblingIgnoringCertainElements(Element startingEl, String[] elementsToIgnore){
		ParentNode parent = startingEl.getParent();
		if (parent==null){
			return null;
		}
		int i = parent.indexOf(startingEl);
		if (i==0) return null;
		Element previous =(Element)parent.getChild(i-1);
		String elName =previous.getLocalName();
		for (String namesToIgnore : elementsToIgnore) {
			if (elName.equals(namesToIgnore)){
				return getPreviousSiblingIgnoringCertainElements(previous, elementsToIgnore);
			}
		}
		return previous;
	}

	/**
	 * Finds all descendant elements whose localname matches the given elementName
	 * Equivalent to an xpath of type .//*[local-name() = 'elementName'] from the startingElement
	 * @param startingElement
	 * @param elementName
	 * @return
	 */
	public static List<Element> getDescendantElementsWithTagName(Element startingElement, String elementName) {
		List<Element> matchingElements = new LinkedList<Element>();
		LinkedList<Element> stack = new LinkedList<Element>();
		Elements children =startingElement.getChildElements();
		for (int i = children.size() -1; i >= 0; i--) {
			stack.add(children.get(i));
		}
		while (stack.size()>0){
			Element currentElement =stack.removeLast();
			if (currentElement.getLocalName().equals(elementName)){
				matchingElements.add(currentElement);
			}
			children =currentElement.getChildElements();
			for (int i = children.size() -1; i >= 0; i--) {
				Element child =children.get(i);
				stack.add(child);
			}
		}
		return matchingElements;
	}

	/**
	 * Finds all descendant elements whose localname matches one of the strings in elementNames
	 * Equivalent to an xpath of type .//*[local-name() = 'elementName1']|.//*[local-name() = 'elementName2']|.//*[local-name() = 'elementName3'] from the startingElement
	 * @param startingElement
	 * @param elementNames
	 * @return
	 */
	public static List<Element> getDescendantElementsWithTagNames(Element startingElement, String[] elementNames) {
		List<Element> matchingElements = new LinkedList<Element>();
		LinkedList<Element> stack = new LinkedList<Element>();
		Elements children =startingElement.getChildElements();
		for (int i = children.size() -1; i >= 0; i--) {
			stack.add(children.get(i));
		}
		while (stack.size()>0){
			Element currentElement =stack.removeLast();
			String currentElName=currentElement.getLocalName();
			for (String targetTagName : elementNames) {
				if (currentElName.equals(targetTagName)){
					matchingElements.add(currentElement);
					break;
				}
			}
			children =currentElement.getChildElements();
			for (int i = children.size() -1; i >= 0; i--) {
				Element child =children.get(i);
				stack.add(child);
			}
		}
		return matchingElements;
	}

	/**
	 * Finds all child elements whose localname matches one of the strings in elementNames
	 * Equivalent to an xpath of type ./*[local-name() = 'elementName1']|./*[local-name() = 'elementName2']|./*[local-name() = 'elementName3'] from the startingElement
	 * @param startingElement
	 * @param elementNames
	 * @return
	 */
	public static List<Element> getChildElementsWithTagNames(Element startingElement, String[] elementNames) {
		List<Element> matchingElements = new LinkedList<Element>();
		Elements children =startingElement.getChildElements();
		int childCount = children.size();
		for (int i = 0; i < childCount; i++) {
			Element child =children.get(i);
			String currentElName=child.getLocalName();
			for (String targetTagName : elementNames) {
				if (currentElName.equals(targetTagName)){
					matchingElements.add(child);
					break;
				}
			}
		}
		return matchingElements;
	}

	/**
	 * Finds all child elements whose localname matches one of the strings in elementNames
	 * Equivalent to an xpath of type ./*[local-name() = 'elementName'] from the startingElement
	 * This is equivalent to XOM's getChildElements(String) other than returning an LinkedList
	 * @param startingElement
	 * @param elementName
	 * @return
	 */
	public static List<Element> getChildElementsWithTagName(Element startingElement, String elementName) {
		List<Element> matchingElements = new LinkedList<Element>();
		Elements children =startingElement.getChildElements();
		int childCount = children.size();
		for (int i = 0; i < childCount; i++) {
			Element child =children.get(i);
			String currentElName=child.getLocalName();
			if (currentElName.equals(elementName)){
				matchingElements.add(child);
			}
		}
		return matchingElements;
	}

	/**
	 * Finds all descendant elements whose localname matches the given elementName
	 * Additionally the element must have the specified attribute and the value of the attribute must be as specified
	 * Equivalent to an xpath of type .//*[local-name() = 'elementName'][@attribute="attributevalue"] from the startingElement
	 * @param startingElement
	 * @param elementName
	 * @return
	 */
	public static List<Element> getDescendantElementsWithTagNameAndAttribute(Element startingElement, String elementName, String attributeName, String attributeValue) {
		List<Element> matchingElements = new LinkedList<Element>();
		LinkedList<Element> stack = new LinkedList<Element>();
		Elements children =startingElement.getChildElements();
		for (int i = children.size() -1; i >= 0; i--) {
			stack.add(children.get(i));
		}
		while (stack.size()>0){
			Element currentElement =stack.removeLast();
			if (currentElement.getLocalName().equals(elementName)){
				if (attributeValue.equals(currentElement.getAttributeValue(attributeName))){
					matchingElements.add(currentElement);
				}
			}
			children =currentElement.getChildElements();
			for (int i = children.size() -1; i >= 0; i--) {
				Element child =children.get(i);
				stack.add(child);
			}
		}
		return matchingElements;
	}

	/**
	 * Finds all child elements whose localname matches the given elementName
	 * Additionally the element must have the specified attribute and the value of the attribute must be as specified
	 * Equivalent to an xpath of type ./*[local-name() = 'elementName'][@attribute="attributevalue"] from the startingElement
	 * @param startingElement
	 * @param elementName
	 * @return
	 */
	public static List<Element> getChildElementsWithTagNameAndAttribute(Element startingElement, String elementName, String attributeName, String attributeValue) {
		List<Element> matchingElements = new LinkedList<Element>();
		Elements children =startingElement.getChildElements();
		for (int i = 0; i < children.size(); i++) {
			Element child =children.get(i);
			if (child.getLocalName().equals(elementName)){
				if (attributeValue.equals(child.getAttributeValue(attributeName))){
					matchingElements.add(child);
				}
			}
		}
		return matchingElements;
	}

	/**
	 * Finds and returns the number of elements and the number of elements with no children, that are descendants of the startingElement
	 * The 0th position of the returned array is the total number of elements
	 * The 1st position is the number of child less elements
	 * @param startingElement
	 * @return
	 */
	public static int[] countNumberOfElementsAndNumberOfChildLessElements(Element startingElement) {
		int[] counts = new int[2];
		LinkedList<Element> stack = new LinkedList<Element>();
		stack.add(startingElement);
		while (stack.size()>0){
			Element currentElement =stack.removeLast();
			Elements children =currentElement.getChildElements();
			int childCount = children.size();
			if (childCount==0){
				counts[1]++;
			}
			else{
				for (int i = 0; i < childCount; i++) {
					counts[0]++;
					stack.add(children.get(i));
				}
			}
		}
		return counts;
	}

	/**
	 * Find all the later siblings of startingElement with the search terminating at the element with string tagName
	 * or if there are not more siblings
	 * @param startingEl
	 * @param tagName
	 * @return
	 */
	public static List<Element> getSiblingsUpToElementWithTagName(Element startingEl, String tagName) {
		List<Element> laterSiblings = new LinkedList<Element>();
		Element nextEl = (Element) XOMWrapper.getNextSibling(startingEl);
		while (nextEl !=null && !nextEl.getLocalName().equals(tagName)){
			laterSiblings.add(nextEl);
			nextEl = (Element) XOMWrapper.getNextSibling(nextEl);
		}
		return laterSiblings;
	}
}
