//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.07.20 at 10:40:55 AM CEST 
//


package groove.io.conceptual.configuration.schema;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the groove.io.abstraction.configuration.schema package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: groove.io.abstraction.configuration.schema
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TypeModel.Properties }
     * 
     */
    public TypeModel.Properties createTypeModelProperties() {
        return new TypeModel.Properties();
    }

    /**
     * Create an instance of {@link Global.IdOverrides }
     * 
     */
    public Global.IdOverrides createGlobalIdOverrides() {
        return new Global.IdOverrides();
    }

    /**
     * Create an instance of {@link TypeModel.Constraints }
     * 
     */
    public TypeModel.Constraints createTypeModelConstraints() {
        return new TypeModel.Constraints();
    }

    /**
     * Create an instance of {@link TypeModel.Fields }
     * 
     */
    public TypeModel.Fields createTypeModelFields() {
        return new TypeModel.Fields();
    }

    /**
     * Create an instance of {@link Global }
     * 
     */
    public Global createGlobal() {
        return new Global();
    }

    /**
     * Create an instance of {@link TypeModel }
     * 
     */
    public TypeModel createTypeModel() {
        return new TypeModel();
    }

    /**
     * Create an instance of {@link Configuration }
     * 
     */
    public Configuration createConfiguration() {
        return new Configuration();
    }

    /**
     * Create an instance of {@link TypeModel.Fields.Containers.Ordering }
     * 
     */
    public TypeModel.Fields.Containers.Ordering createTypeModelFieldsContainersOrdering() {
        return new TypeModel.Fields.Containers.Ordering();
    }

    /**
     * Create an instance of {@link TypeModel.Fields.Intermediates }
     * 
     */
    public TypeModel.Fields.Intermediates createTypeModelFieldsIntermediates() {
        return new TypeModel.Fields.Intermediates();
    }

    /**
     * Create an instance of {@link TypeModel.Fields.Defaults }
     * 
     */
    public TypeModel.Fields.Defaults createTypeModelFieldsDefaults() {
        return new TypeModel.Fields.Defaults();
    }

    /**
     * Create an instance of {@link StringsType }
     * 
     */
    public StringsType createStringsType() {
        return new StringsType();
    }

    /**
     * Create an instance of {@link TypeModel.Fields.Containers }
     * 
     */
    public TypeModel.Fields.Containers createTypeModelFieldsContainers() {
        return new TypeModel.Fields.Containers();
    }

    /**
     * Create an instance of {@link InstanceModel }
     * 
     */
    public InstanceModel createInstanceModel() {
        return new InstanceModel();
    }

    /**
     * Create an instance of {@link InstanceModel.Objects }
     * 
     */
    public InstanceModel.Objects createInstanceModelObjects() {
        return new InstanceModel.Objects();
    }

}
