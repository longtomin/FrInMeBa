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
 *         &lt;element name="CN" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ICID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element ref="{}OU"/>
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
    "cn",
    "cid",
    "icid",
    "ou"
})
@XmlRootElement(name = "C")
public class C {

    @XmlElement(name = "CN", required = true)
    protected String cn;
    @XmlElement(name = "CID")
    protected int cid;
    @XmlElement(name = "ICID")
    protected int icid;
    @XmlElement(name = "OU", required = true)
    protected OU ou;

    /**
     * Gets the value of the cn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCN() {
        return cn;
    }

    /**
     * Sets the value of the cn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCN(String value) {
        this.cn = value;
    }

    /**
     * Gets the value of the cid property.
     * 
     */
    public int getCID() {
        return cid;
    }

    /**
     * Sets the value of the cid property.
     * 
     */
    public void setCID(int value) {
        this.cid = value;
    }

    /**
     * Gets the value of the icid property.
     * 
     */
    public int getICID() {
        return icid;
    }

    /**
     * Sets the value of the icid property.
     * 
     */
    public void setICID(int value) {
        this.icid = value;
    }

    /**
     * Gets the value of the ou property.
     * 
     * @return
     *     possible object is
     *     {@link OU }
     *     
     */
    public OU getOU() {
        return ou;
    }

    /**
     * Sets the value of the ou property.
     * 
     * @param value
     *     allowed object is
     *     {@link OU }
     *     
     */
    public void setOU(OU value) {
        this.ou = value;
    }

}