package tr.gov.gib.evdbelge.evdbelgeaktarma.config;

/**

mapperda bos string i null a cevirme ise yaradi. ama bu da baska bir durumda faydali olabilir. Entity uzerinde degisiklik yapmiyor. insert ederken direkt kolon uzerinden gibi.

Kullanim:
autoApply = true ise otomatik tum fieldlari convert ediyor.
autoApply = false ise

@Convert(converter = EmptyStringToNullConverter.class)
@Column(name = "vergi_no")
private String vergiNo;


 */

import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class EmptyStringToNullConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String string) {
        // Use defaultIfEmpty to preserve Strings consisting only of whitespaces
        return StringUtils.defaultIfBlank(string, null);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        //If you want to keep it null otherwise transform to empty String
        return dbData;
    }
}
