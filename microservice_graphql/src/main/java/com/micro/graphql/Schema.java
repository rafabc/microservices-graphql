package com.micro.graphql;


import graphql.schema.DataFetcher;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.TypeResolver;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.micro.graphql.services.CharacterRepository;
import com.micro.graphql.services.EpisodeRepository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class Schema {

    public static class MutationResult {
        public boolean success = true;
    }


    @Autowired
    CharacterRepository characterRepository;

    @Autowired
    EpisodeRepository episodeRepository;

    GraphQLSchema graphQLSchema;

    @PostConstruct
    public void init() {
        try {
            SchemaParser schemaParser = new SchemaParser();
            ClassPathResource classPathResource = new ClassPathResource("schema.graphql");
            TypeDefinitionRegistry compiledSchema = schemaParser.parse(classPathResource.getFile());

            SchemaGenerator schemaGenerator = new SchemaGenerator();
            graphQLSchema = schemaGenerator.makeExecutableSchema(compiledSchema, buildRuntimeWiring());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public GraphQLSchema getSchema() {
        return graphQLSchema;
    }

    DataFetcher<CharacterRepository.Character> characterDataFetcher = environment ->
            characterRepository.getCharacterByName(environment.getArgument("firstName"));

    DataFetcher<List<CharacterRepository.Character>> allCharacters = environment -> characterRepository.getAll();
    DataFetcher<List<EpisodeRepository.Episode>> allEpisodes = environment -> episodeRepository.getAll();

    DataFetcher<String> getSeason = environment -> {
        EpisodeRepository.Episode episode = environment.getSource();
        return episode.season.name();
    };

    DataFetcher<List<CharacterRepository.Character>> getCharactersForEpisode = environment -> {
        EpisodeRepository.Episode episode = environment.getSource();
        return characterRepository.getCharactersById(episode.characterRefs);
    };

    DataFetcher<List<EpisodeRepository.Episode>> episodesForCharacter = environment -> {
        CharacterRepository.Character character = environment.getSource();
        return episodeRepository.getEpisodesWithCharacter(character.id);
    };

    DataFetcher<MutationResult> addCharacter = environment -> {
        Map<String, Object> character = environment.getArgument("character");
        String firstName = (String) character.get("firstName");
        String lastName = (String) character.get("lastName");
        Boolean family = (Boolean) character.get("family");
        characterRepository.addCharacter(firstName, lastName, family != null ? family : false);
        return new MutationResult();
    };

    /**
     * Explicit DataFetcher: It this case it is just an example and not needed because the default DataFetcher
     * will look for a property with the name of the field in the source object (which is a Character)
     */
    DataFetcher<String> firstName = environment -> ((CharacterRepository.Character) environment.getSource()).firstName;

    DataFetcher<Object> search = environment -> {
        String searchFor = environment.getArgument("searchFor");
        List<CharacterRepository.Character> characters = characterRepository.search(searchFor);
        List<EpisodeRepository.Episode> episodes = episodeRepository.search(searchFor);
        List<Object> result = new ArrayList<>();
        result.addAll(characters);
        result.addAll(episodes);
        return result;
    };

    TypeResolver everythingResolver = env -> {
        if (env.getObject() instanceof CharacterRepository.Character) {
            return (GraphQLObjectType) graphQLSchema.getType("Character");
        } else if (env.getObject() instanceof EpisodeRepository.Episode) {
            return (GraphQLObjectType) graphQLSchema.getType("Episode");
        } else {
            throw new RuntimeException("Uknown type" + env.getObject());
        }
    };


    private RuntimeWiring buildRuntimeWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .type("MutationType", typeWiring -> typeWiring
                        .dataFetcher("addCharacter", addCharacter)
                ).type("QueryType", typeWiring -> typeWiring
                        .dataFetcher("character", characterDataFetcher)
                        .dataFetcher("characters", allCharacters)
                        .dataFetcher("episodes", allEpisodes)
                        .dataFetcher("search", search)
                ).type("Episode", typeWiring -> typeWiring
                        .dataFetcher("season", getSeason)
                        .dataFetcher("characters", getCharactersForEpisode)
                ).type("Character", typeWiring -> typeWiring
                        .dataFetcher("episodes", episodesForCharacter)
                        .dataFetcher("firstName", firstName)
                ).type("Everything", typeWiring -> typeWiring
                        .typeResolver(everythingResolver)
                ).build();
    }

}
