package com.pbs.backend;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.embedding.EmbeddingModel;
import javax.sql.DataSource;

@SpringBootTest
class BackendApplicationTests {

	@MockitoBean
	private DataSource dataSource;

	@MockitoBean
	private VectorStore vectorStore;

	@MockitoBean
	private EmbeddingModel embeddingModel;

	@Test
	@DisplayName("Prueba de humo: El contexto de Spring debe cargar correctamente")
	void contextLoads() {
		// Pasa automáticamente si toda la inyección de dependencias es correcta
	}
}
