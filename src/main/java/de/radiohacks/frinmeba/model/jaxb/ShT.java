//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.03.26 at 08:36:09 PM CET 
//


package de.radiohacks.frinmeba.model.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="T" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "mid",
    "t"
})
@XmlRootElement(name = "ShT")
public class ShT {

    @XmlElement(name = "MID")
    protected int mid;
    @XmlElement(name = "T")
    protected long t;

    /**
     * Gets the value of the mid property.
     * 
     */
    public int getMID() {
        return mid;
    }

    /**
     * Sets the value of the mid property.
     * 
     */
    public void setMID(int value) {
        this.mid = value;
    }

    /**
     * Gets the value of the t property.
     * 
     */
    public long getT() {
        return t;
    }

    /**
     * Sets the value of the t property.
     * 
     */
    public void setT(long value) {
        this.t = value;
    }

}