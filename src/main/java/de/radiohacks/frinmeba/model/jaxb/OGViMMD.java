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
 *       &lt;choice>
 *         &lt;sequence>
 *           &lt;element name="VM" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *           &lt;element name="VS" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *           &lt;element name="VMD5" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "vm",
    "vs",
    "vmd5",
    "et"
})
@XmlRootElement(name = "OGViMMD")
public class OGViMMD {

    @XmlElement(name = "VM")
    protected String vm;
    @XmlElement(name = "VS")
    protected Long vs;
    @XmlElement(name = "VMD5")
    protected String vmd5;
    @XmlElement(name = "ET")
    protected String et;

    /**
     * Gets the value of the vm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVM() {
        return vm;
    }

    /**
     * Sets the value of the vm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVM(String value) {
        this.vm = value;
    }

    /**
     * Gets the value of the vs property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getVS() {
        return vs;
    }

    /**
     * Sets the value of the vs property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setVS(Long value) {
        this.vs = value;
    }

    /**
     * Gets the value of the vmd5 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVMD5() {
        return vmd5;
    }

    /**
     * Sets the value of the vmd5 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVMD5(String value) {
        this.vmd5 = value;
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
