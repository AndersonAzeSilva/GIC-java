package br.com.megacenter.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImportacaoArquivoStore {

    private static final String PASTA = "importacoes_excel";

    public static Path salvarCopia(File arquivoOriginal) throws IOException {

        Path dir = Paths.get(PASTA);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nomeSeguro = arquivoOriginal.getName().replaceAll("[^a-zA-Z0-9._-]", "_");

        Path destino = dir.resolve(ts + "_" + nomeSeguro);

        Files.copy(arquivoOriginal.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);

        return destino;
    }
}
