//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.05.30 at 08:04:57 PM CEST 
//


package de.radiohacks.frinmeba.modelshort;

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
 *         &lt;element name="UN" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PW" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="IID" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "un",
    "pw",
    "iid"
})
@XmlRootElement(name = "IGImMMD")
public class IGImMMD {

    @XmlElement(name = "UN", required = true)
    protected String un;
    @XmlElement(name = "PW", required = true)
    protected String pw;
    @XmlElement(name = "IID")
    protected int iid;

    /**
     * Gets the value of the un property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUN() {
        return un;
    }

    /**
     * Sets the value of the un property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUN(String value) {
        this.un = value;
    }

    /**
     * Gets the value of the pw property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPW() {
        return pw;
    }

    /**
     * Sets the value of the pw property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPW(String value) {
        this.pw = value;
    }

    /**
     * Gets the value of the iid property.
     * 
     */
    public int getIID() {
        return iid;
    }

    /**
     * Sets the value of the iid property.
     * 
     */
    public void setIID(int value) {
        this.iid = value;
    }

}
