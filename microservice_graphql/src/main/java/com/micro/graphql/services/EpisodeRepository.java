package com.micro.graphql.services;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EpisodeRepository {

    public static enum Season {
        Season1,
        Season2,
        Season3,
        Season4,
        Season5,
        Season6,
        Season7,
        Season8,
        Season9
    }

    public static class Episode {

        public String id;
        public String name;
        public int number;
        public int numberOverall;
        public Season season;


        public Episode(String id, String name, int number, int numberOverall, Season season) {
            this.id = id;
            this.name = name;
            this.number = number;
            this.numberOverall = numberOverall;
            this.season = season;
        }

        public List<String> characterRefs = new ArrayList<>();

        void addCharacter(String... characterId) {
            this.characterRefs.addAll(Arrays.asList(characterId));
        }

    }

    private List<Episode> episodes = new ArrayList<>();

    @PostConstruct
    public void init() {
        Episode epi1 = new Episode("1", "Episode 1 - The phantom menace", 1, 1, Season.Season1);
        Episode epi2 = new Episode("2", "Episode 2 - Attack of the clones", 2, 2, Season.Season2);
        Episode epi3 = new Episode("3", "Episode 3 - Revenge of sith", 3, 3, Season.Season3);
        Episode epi4 = new Episode("4", "Episode 4 - A new hope", 18, 77, Season.Season4);
        Episode epi5 = new Episode("5", "Episode 5 - The empire strikes back", 18, 77, Season.Season4);
        Episode epi6 = new Episode("6", "Episode 6 - Return of the jedi", 18, 77, Season.Season4);
        Episode epi7 = new Episode("7", "Episode 7 - The force awakens", 18, 77, Season.Season4);
        Episode epi8 = new Episode("8", "Episode 8 - The last jedi", 18, 77, Season.Season4);

        episodes.add(epi1);
        epi1.addCharacter("1", "2");
        
        episodes.add(epi2);
        epi2.addCharacter("1", "2", "3");

        episodes.add(epi3);
        epi3.addCharacter("1", "2");

        episodes.add(epi4);
        epi4.addCharacter("1", "2", "3", "6");
        
        episodes.add(epi5);
        epi5.addCharacter("1", "4", "8");
        
        episodes.add(epi6);
        epi6.addCharacter("2", "4", "5");
        
        episodes.add(epi7);
        epi7.addCharacter("6", "7");
        
        episodes.add(epi8);
        epi8.addCharacter("4", "8");

    }

    public List<Episode> getAll() {
        return this.episodes;
    }

    public List<Episode> getEpisodesWithCharacter(String id) {
        return episodes.stream().filter(episode -> episode.characterRefs.contains(id)).collect(Collectors.toList());
    }

    public List<Episode> search(String searchFor) {
        List<Episode> found = episodes.stream().filter(episode -> episode.name.contains(searchFor)).collect(Collectors.toList());
        return found;
    }


}
