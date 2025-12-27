package br.com.megacenter.services;

import java.io.File;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ImportacaoArquivoStore {

    private static final String PASTA = "importacoes_excel";

    public static Path salvarCopia(File arquivoOriginal) throws Exception {

        if (arquivoOriginal == null || !arquivoOriginal.exists()) {
            throw new Exception("Arquivo não encontrado para salvar cópia.");
        }

        Path dir = Paths.get(PASTA);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        String safeName = arquivoOriginal.getName().replaceAll("[^a-zA-Z0-9._-]", "_");
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String novoNome = ts + "__" + safeName;

        Path destino = dir.resolve(novoNome);
        Files.copy(arquivoOriginal.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);

        return destino;
    }
}
