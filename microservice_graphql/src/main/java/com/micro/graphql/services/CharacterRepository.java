package com.micro.graphql.services;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CharacterRepository {


    public static class Character {

        public Character(String id, String firstName, String lastName, boolean family) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.family = family;
        }

        public String id;
        public String firstName;
        public String lastName;
        public boolean family;


    }


    private List<Character> characters = new ArrayList<>();

    private int id = 1;

    private String nextId() {
        return Integer.toString(id++);
    }

    @PostConstruct
    public void init() {
        Character ovi = new Character(nextId(), "Obi-wane", "Quenovi", true);
        Character chiwi = new Character(nextId(), "Han", "Solo", true);
        Character luke = new Character(nextId(), "Luke", "Skywalker", true);
        Character leya = new Character(nextId(), "Leya", "Organa", true);
        Character yoda = new Character(nextId(), "Kylo", "Ren", true);
        Character darth = new Character(nextId(), "Darth", "Vader", false);

        characters.add(ovi);
        characters.add(chiwi);
        characters.add(luke);
        characters.add(leya);
        characters.add(yoda);
        characters.add(darth);
    }

    public List<Character> search(String searchFor) {
        List<Character> found = characters.stream().filter(character ->
                character.firstName.contains(searchFor) || character.lastName.contains(searchFor)).collect(Collectors.toList());
        return found;
    }

    public void addCharacter(String firstName, String lastName, boolean family) {
        characters.add(new Character(nextId(), firstName, lastName, family));
    }


    public Character getCharacter(String id) {
        List<Character> found = characters.stream().filter(character -> character.id.equals(id)).collect(Collectors.toList());
        return found.size() > 0 ? found.get(0) : null;
    }

    public Character getCharacterByName(String firstName) {
        List<Character> found = characters.stream().filter(character -> character.firstName.equals(firstName)).collect(Collectors.toList());
        return found.size() > 0 ? found.get(0) : null;
    }

    public List<Character> getCharactersById(List<String> ids) {
        List<Character> found = characters.stream().filter(character -> ids.contains(character.id)).collect(Collectors.toList());
        return found;
    }

    public List<Character> getAll() {
        return characters;
    }
}
