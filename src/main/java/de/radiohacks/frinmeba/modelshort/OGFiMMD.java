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
 *       &lt;choice>
 *         &lt;sequence>
 *           &lt;element name="FM" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *           &lt;element name="FS" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *           &lt;element name="FMD5" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;/sequence>
 *         &lt;element name="ET" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "fm",
    "fs",
    "fmd5",
    "et"
})
@XmlRootElement(name = "OGFiMMD")
public class OGFiMMD {

    @XmlElement(name = "FM")
    protected String fm;
    @XmlElement(name = "FS")
    protected Long fs;
    @XmlElement(name = "FMD5")
    protected String fmd5;
    @XmlElement(name = "ET")
    protected String et;

    /**
     * Gets the value of the fm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFM() {
        return fm;
    }

    /**
     * Sets the value of the fm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFM(String value) {
        this.fm = value;
    }

    /**
     * Gets the value of the fs property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getFS() {
        return fs;
    }

    /**
     * Sets the value of the fs property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setFS(Long value) {
        this.fs = value;
    }

    /**
     * Gets the value of the fmd5 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFMD5() {
        return fmd5;
    }

    /**
     * Sets the value of the fmd5 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFMD5(String value) {
        this.fmd5 = value;
    }

    /**
     * Gets the value of the et property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getET() {
        return et;
    }

    /**
     * Sets the value of the et property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setET(String value) {
        this.et = value;
    }

}
