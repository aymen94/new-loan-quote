package xyz.aymen.newloanquote.util;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import xyz.aymen.newloanquote.model.Lender;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class FileUtils {

    public static List<Lender> convertFileToModel(byte[] data) {
        List<Lender> lenderList = null;
        Reader targetReader = new StringReader(new String(data));
        lenderList = new CsvToBeanBuilder(targetReader)
                .withType(Lender.class)
                .withThrowExceptions(false)
                .build()
                .parse();
        return lenderList;
    }


}
