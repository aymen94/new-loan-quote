package xyz.aymen.newloanquote.util;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import xyz.aymen.newloanquote.model.Lender;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {

    public static List<Lender> convertFileToModel(byte[] data) {
        List<Lender> lenderList = new ArrayList<>(1);
        try {
            Reader targetReader = new StringReader(new String(data));
            lenderList = new CsvToBeanBuilder(targetReader)
                    .withType(Lender.class)
                    .withThrowExceptions(true)
                    .build()
                    .parse();
        } catch (RuntimeException e) {

        }
        return lenderList;
    }


}
