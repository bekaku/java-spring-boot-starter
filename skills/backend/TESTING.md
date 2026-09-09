# Testing / Verification

Read this before final validation of implementation work.

**## 10. Testing Requirements**

- Location: `src/test/java/com/bekaku/api/spring/` (`controller/api/`, `serviceImpl/`). Framework: JUnit Jupiter + Mockito (`@Mock/@InjectMocks`, `MockitoSettings(LENIENT)`) + AssertJ; no `@SpringBootTest/@WebMvcTest/@WithMockUser` in current suite despite `spring-security-test` + `spring-restdocs-mockmvc` deps.

- What/how/where:

  | Change | Required evidence |

  |---|---|

  | Controller/service logic | Focused unit test incl. failure path + ownership (`AiChatControllerOwnershipTest`, `AiRagChatServiceOwnershipTest`, `FaceRecognitionServiceOwnershipTest` pattern: assert `findByIdAndCreator` scoping, `verifyNoInteractions` on bypass) |

  | Auth/routes | HTTP test through real filter chain + cookie AND Bearer flows (current `AuthControllerTest` constructs controller directly — insufficient for chain enforcement) |

  | SQL/schema/vector columns | Disposable PostgreSQL + pgvector integration test (H2 invalid for casts/vector ops/dump migrations) |

  | File ops | `@TempDir` containment/ownership/partial-failure tests (`FileManagerControllerTest` standalone MockMvc pattern) |

  | RAG/SSE | Mock model/vector deps; verify event order, disabled mode, cancellation/error, persistence |

  | Config/wiring | `compileJava`/context-startup test |

  | Docs only | Verify statements/commands/links/whitespace; claim no runtime validation |

- Commands: `./gradlew test --tests '*AuthControllerTest*'`, `./gradlew test`, `./gradlew compileJava`, `./gradlew bootJar`, `./gradlew bootRun --args='--spring.profiles.active=dev'` (needs PG/Qdrant/Ollama infra). Note: `--tests` filters execution, not test-source compilation.

- Report changed files, behavior deltas, exact commands + results, remaining integration limits. Never claim deployment/security/success without verification.
