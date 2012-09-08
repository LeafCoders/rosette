package se.ryttargardskyrkan.rosette.integration.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import se.ryttargardskyrkan.rosette.model.Theme;

public class ThemeTestUtil {
	public static List<Theme> ThemeResponseToThemeList(HttpResponse response) throws IllegalStateException, IOException {
		String json = IOUtils.toString(response.getEntity().getContent(), "utf-8");
		return jsonToThemeList(json);
	}
	
	public static Theme ThemeResponseToTheme(HttpResponse response) throws IllegalStateException, IOException {
		String json = IOUtils.toString(response.getEntity().getContent(), "utf-8");
		return jsonToTheme(json);
	}
	
	public static List<Theme> jsonToThemeList(String json) throws JsonParseException, JsonMappingException, IOException {
		return new ObjectMapper().readValue(json, new TypeReference<ArrayList<Theme>>() {});
	}
	
	public static Theme jsonToTheme(String json) throws JsonParseException, JsonMappingException, IOException {
		return new ObjectMapper().readValue(json, new TypeReference<Theme>() {});
	}
	
	public static String ThemeToJson(Theme Theme) throws JsonGenerationException, JsonMappingException, IOException {
		return new ObjectMapper().writeValueAsString(Theme);
	}
	
	public static String ThemeListToJson(List<Theme> ThemeList) throws JsonGenerationException, JsonMappingException, IOException {
		return new ObjectMapper().writeValueAsString(ThemeList);
	}

	public static void assertThemeListResponseBodyIsCorrect(String expectedThemes, HttpResponse response) throws IllegalStateException, IOException {
		ObjectMapper mapper = new ObjectMapper();

		// Expected
		List<Theme> expectedThemesAsList = mapper.readValue(expectedThemes, new TypeReference<ArrayList<Theme>>() {});
		String expectedThemesAsString = mapper.writeValueAsString(expectedThemesAsList);

		// Actual
		List<Theme> actualThemesAsList = ThemeResponseToThemeList(response);
//		for (Theme Theme : actualThemesAsList) {
//			Theme.setId(null);
//		}
		String actualThemesAsString = mapper.writeValueAsString(actualThemesAsList);

		assertEquals(expectedThemesAsString, actualThemesAsString);
	}
	
	public static void assertThemeResponseBodyIsCorrect(String expectedTheme, HttpResponse response) throws IllegalStateException, IOException {
		ObjectMapper mapper = new ObjectMapper();

		// Expected
		Theme expectedThemeAsTheme = mapper.readValue(expectedTheme, new TypeReference<Theme>() {});
		String expectedThemesAsString = mapper.writeValueAsString(expectedThemeAsTheme);

		// Actual
		Theme actualTheme = ThemeResponseToTheme(response);
		actualTheme.setId(null);
		String actualThemesAsString = mapper.writeValueAsString(actualTheme);

		assertEquals(expectedThemesAsString, actualThemesAsString);
	}
	
	public static void assertThemeIsCorrect(String expectedTheme, Theme actualTheme) throws IllegalStateException, IOException {
		String expectedThemeAsString = ThemeToJson(jsonToTheme(expectedTheme));
		String actualThemeAsString = ThemeToJson(actualTheme);
		assertEquals(expectedThemeAsString, actualThemeAsString);
	}
	
	public static void assertThemeWithNoIdIsCorrect(String expectedTheme, Theme actualTheme) throws IllegalStateException, IOException {
		String expectedThemeAsString = ThemeToJson(jsonToTheme(expectedTheme));
		actualTheme.setId(null);
		String actualThemeAsString = ThemeToJson(actualTheme);
		assertEquals(expectedThemeAsString, actualThemeAsString);
	}
	
	public static void assertThemeListIsCorrect(String expectedThemes, List<Theme> actualThemeList) throws IllegalStateException, IOException {
		String expectedThemesAsString = ThemeListToJson(jsonToThemeList(expectedThemes));
		String actualThemesAsString = ThemeListToJson(actualThemeList);
		assertEquals(expectedThemesAsString, actualThemesAsString);
	}
}
