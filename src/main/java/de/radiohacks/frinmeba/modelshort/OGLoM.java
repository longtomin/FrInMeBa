/**
 * Copyright � 2015, Thomas Schreiner, thomas1.schreiner@googlemail.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.06.02 at 09:26:35 PM CEST 
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
 *           &lt;element name="LM">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                   &lt;sequence>
 *                     &lt;element name="Lat" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                     &lt;element name="Lon" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;/sequence>
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
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
    "lm",
    "et"
})
@XmlRootElement(name = "OGLoM")
public class OGLoM {

    @XmlElement(name = "LM")
    protected OGLoM.LM lm;
    @XmlElement(name = "ET")
    protected String et;

    /**
     * Gets the value of the lm property.
     * 
     * @return
     *     possible object is
     *     {@link OGLoM.LM }
     *     
     */
    public OGLoM.LM getLM() {
        return lm;
    }

    /**
     * Sets the value of the lm property.
     * 
     * @param value
     *     allowed object is
     *     {@link OGLoM.LM }
     *     
     */
    public void setLM(OGLoM.LM value) {
        this.lm = value;
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
     *         &lt;element name="Lat" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="Lon" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "lat",
        "lon"
    })
    public static class LM {

        @XmlElement(name = "Lat", required = true)
        protected String lat;
        @XmlElement(name = "Lon", required = true)
        protected String lon;

        /**
         * Gets the value of the lat property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLat() {
            return lat;
        }

        /**
         * Sets the value of the lat property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLat(String value) {
            this.lat = value;
        }

        /**
         * Gets the value of the lon property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLon() {
            return lon;
        }

        /**
         * Sets the value of the lon property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLon(String value) {
            this.lon = value;
        }

    }

}
