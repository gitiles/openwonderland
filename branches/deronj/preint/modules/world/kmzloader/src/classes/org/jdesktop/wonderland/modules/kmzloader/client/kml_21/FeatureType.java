//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.09.19 at 09:37:59 AM PDT 
//


package org.jdesktop.wonderland.modules.kmzloader.client.kml_21;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FeatureType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FeatureType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://earth.google.com/kml/2.1}ObjectType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="visibility" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="open" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="address" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phoneNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Snippet" type="{http://earth.google.com/kml/2.1}SnippetType" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element ref="{http://earth.google.com/kml/2.1}LookAt" minOccurs="0"/>
 *         &lt;element ref="{http://earth.google.com/kml/2.1}TimePrimitive" minOccurs="0"/>
 *         &lt;element ref="{http://earth.google.com/kml/2.1}styleUrl" minOccurs="0"/>
 *         &lt;element ref="{http://earth.google.com/kml/2.1}StyleSelector" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://earth.google.com/kml/2.1}Region" minOccurs="0"/>
 *         &lt;element name="Metadata" type="{http://earth.google.com/kml/2.1}MetadataType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FeatureType", propOrder = {
    "name",
    "visibility",
    "open",
    "address",
    "phoneNumber",
    "snippet",
    "description",
    "lookAt",
    "timePrimitive",
    "styleUrl",
    "styleSelector",
    "region",
    "metadata"
})
@XmlSeeAlso({
    NetworkLinkType.class,
    PlacemarkType.class,
    ContainerType.class,
    OverlayType.class
})
public abstract class FeatureType
    extends ObjectType
{

    protected String name;
    @XmlElement(defaultValue = "1")
    protected Boolean visibility;
    @XmlElement(defaultValue = "1")
    protected Boolean open;
    protected String address;
    protected String phoneNumber;
    @XmlElement(name = "Snippet")
    protected SnippetType snippet;
    protected String description;
    @XmlElement(name = "LookAt")
    protected LookAtType lookAt;
    @XmlElementRef(name = "TimePrimitive", namespace = "http://earth.google.com/kml/2.1", type = JAXBElement.class)
    protected JAXBElement<? extends TimePrimitiveType> timePrimitive;
    @XmlSchemaType(name = "anyURI")
    protected String styleUrl;
    @XmlElementRef(name = "StyleSelector", namespace = "http://earth.google.com/kml/2.1", type = JAXBElement.class)
    protected List<JAXBElement<? extends StyleSelectorType>> styleSelector;
    @XmlElement(name = "Region")
    protected RegionType region;
    @XmlElement(name = "Metadata")
    protected MetadataType metadata;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the visibility property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isVisibility() {
        return visibility;
    }

    /**
     * Sets the value of the visibility property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setVisibility(Boolean value) {
        this.visibility = value;
    }

    /**
     * Gets the value of the open property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isOpen() {
        return open;
    }

    /**
     * Sets the value of the open property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOpen(Boolean value) {
        this.open = value;
    }

    /**
     * Gets the value of the address property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddress(String value) {
        this.address = value;
    }

    /**
     * Gets the value of the phoneNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the value of the phoneNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhoneNumber(String value) {
        this.phoneNumber = value;
    }

    /**
     * Gets the value of the snippet property.
     * 
     * @return
     *     possible object is
     *     {@link SnippetType }
     *     
     */
    public SnippetType getSnippet() {
        return snippet;
    }

    /**
     * Sets the value of the snippet property.
     * 
     * @param value
     *     allowed object is
     *     {@link SnippetType }
     *     
     */
    public void setSnippet(SnippetType value) {
        this.snippet = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the lookAt property.
     * 
     * @return
     *     possible object is
     *     {@link LookAtType }
     *     
     */
    public LookAtType getLookAt() {
        return lookAt;
    }

    /**
     * Sets the value of the lookAt property.
     * 
     * @param value
     *     allowed object is
     *     {@link LookAtType }
     *     
     */
    public void setLookAt(LookAtType value) {
        this.lookAt = value;
    }

    /**
     * Gets the value of the timePrimitive property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link TimePrimitiveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TimeSpanType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TimeStampType }{@code >}
     *     
     */
    public JAXBElement<? extends TimePrimitiveType> getTimePrimitive() {
        return timePrimitive;
    }

    /**
     * Sets the value of the timePrimitive property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link TimePrimitiveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TimeSpanType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TimeStampType }{@code >}
     *     
     */
    public void setTimePrimitive(JAXBElement<? extends TimePrimitiveType> value) {
        this.timePrimitive = ((JAXBElement<? extends TimePrimitiveType> ) value);
    }

    /**
     * Gets the value of the styleUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStyleUrl() {
        return styleUrl;
    }

    /**
     * Sets the value of the styleUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStyleUrl(String value) {
        this.styleUrl = value;
    }

    /**
     * Gets the value of the styleSelector property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the styleSelector property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStyleSelector().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link StyleMapType }{@code >}
     * {@link JAXBElement }{@code <}{@link StyleSelectorType }{@code >}
     * {@link JAXBElement }{@code <}{@link StyleType }{@code >}
     * 
     * 
     */
    public List<JAXBElement<? extends StyleSelectorType>> getStyleSelector() {
        if (styleSelector == null) {
            styleSelector = new ArrayList<JAXBElement<? extends StyleSelectorType>>();
        }
        return this.styleSelector;
    }

    /**
     * Gets the value of the region property.
     * 
     * @return
     *     possible object is
     *     {@link RegionType }
     *     
     */
    public RegionType getRegion() {
        return region;
    }

    /**
     * Sets the value of the region property.
     * 
     * @param value
     *     allowed object is
     *     {@link RegionType }
     *     
     */
    public void setRegion(RegionType value) {
        this.region = value;
    }

    /**
     * Gets the value of the metadata property.
     * 
     * @return
     *     possible object is
     *     {@link MetadataType }
     *     
     */
    public MetadataType getMetadata() {
        return metadata;
    }

    /**
     * Sets the value of the metadata property.
     * 
     * @param value
     *     allowed object is
     *     {@link MetadataType }
     *     
     */
    public void setMetadata(MetadataType value) {
        this.metadata = value;
    }

}
