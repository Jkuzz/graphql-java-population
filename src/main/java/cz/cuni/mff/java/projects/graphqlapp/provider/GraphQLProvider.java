package cz.cuni.mff.java.projects.graphqlapp.provider;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

/**
 * The provider holds an instance of the GraphQL object, which has loaded the necessary data and
 * is ready to execute queries.
 */
public class GraphQLProvider {
    private final GraphQL graphQL;
    private final GraphQLDataFetchers graphQLDataFetchers;

    /**
     * This provides the GraphQL object to be queried
     * @return new GraphQL instance
     */
    public GraphQL getGraphQL() {
        return graphQL;
    }

    /**
     * Creates an instance of data fetchers, reads the schema from resources and builds the wiring.
     */
    public GraphQLProvider() {
        this.graphQLDataFetchers = new GraphQLDataFetchers();
        GraphQLSchema schema = buildSchema(ResourceGetter.getResourceAsString("schema.graphqls"));
        this.graphQL = GraphQL.newGraphQL(schema).build();
    }

    /**
     * Builds the schema using GraphQL-java from the raw schema file contents and the runtime wiring
     * @param sdl The parsed GraphQL schema file
     * @return The build schema
     */
    private GraphQLSchema buildSchema(String sdl) {
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
        RuntimeWiring runtimeWiring = buildWiring();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }


    /**
     * Builds the runtime wiring that is used in the schema generator to create an ExecutableSchema.
     * This links individual queries with their data fetchers, which are provided by graphQLDataFetchers
     * @return The runtime wiring
     */
    private RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .type(newTypeWiring("Query")
                        .dataFetcher("krajById", graphQLDataFetchers.getByIdDataFetcher("kraj")))
                .type(newTypeWiring("Query")
                        .dataFetcher("okresById", graphQLDataFetchers.getByIdDataFetcher("okres")))
                .type(newTypeWiring("Query")
                        .dataFetcher("obecById", graphQLDataFetchers.getByIdDataFetcher("obec")))
                .type(newTypeWiring("Query")
                        .dataFetcher("kraje", graphQLDataFetchers.getKrajeDataFetcher()))
                .type(newTypeWiring("Query")
                        .dataFetcher("okresy", graphQLDataFetchers.getOkresyDataFetcher()))
                .type(newTypeWiring("Query")
                        .dataFetcher("obce", graphQLDataFetchers.getObceDataFetcher()))
                .type(newTypeWiring("Kraj")
                        .dataFetcher("demographics", graphQLDataFetchers.getDemsDataFetcher("100")))
                .type(newTypeWiring("Okres")
                        .dataFetcher("kraj", graphQLDataFetchers.getKrajDataFetcher()))
                .type(newTypeWiring("Okres")
                        .dataFetcher("demographics", graphQLDataFetchers.getDemsDataFetcher("101")))
                .type(newTypeWiring("Obec")
                        .dataFetcher("kraj", graphQLDataFetchers.getKrajDataFetcher()))
                .type(newTypeWiring("Obec")
                        .dataFetcher("okres", graphQLDataFetchers.getOkresDataFetcher()))
                .type(newTypeWiring("Obec")
                        .dataFetcher("demographics", graphQLDataFetchers.getDemsDataFetcher("43")))
                .build();
    }
}
