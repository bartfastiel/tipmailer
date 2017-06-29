/*
 * SonarQube
 * Copyright (C) 2009-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikiReader {

  private static final Pattern TITLE_PATTERN = Pattern.compile("^## (.*)$");

  public static void main(String[] args) throws Exception {
    System.out.println(getTips());
  }

  public static Map<String, String> getTips() throws Exception {
    return pack(parse(getWikiContent()));
  }

  private static List<String> getWikiContent() throws Exception {
    List<String> result = new ArrayList<>();
    URL url = new URL("https://raw.githubusercontent.com/wiki/bartfastiel/tipmailer/Home.md");
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    String line;
    while ((line = rd.readLine()) != null) {
      result.add(line);
    }
    rd.close();
    return result;
  }

  private static Map<String, StringBuilder> parse(List<String> raw) {
    Map<String, StringBuilder> contentBuilders = new HashMap<>();
    String title = null;
    for (String line : raw) {
      Matcher matcher = TITLE_PATTERN.matcher(line);
      if (matcher.matches()) {
        title = matcher.group(1);
      } else if (line.trim().length() > 1 && title != null) {
        StringBuilder stringBuilder = contentBuilders.get(title);
        if (stringBuilder == null) {
          stringBuilder = new StringBuilder();
          contentBuilders.put(title, stringBuilder);
        } else {
          stringBuilder.append('\n');
        }
        stringBuilder.append(line);
      }
    }
    return contentBuilders;
  }

  private static Map<String, String> pack(Map<String, StringBuilder> contentBuilders) {
    Map<String, String> content = new HashMap<>();
    for (Map.Entry entry : contentBuilders.entrySet()) {
      content.put(entry.getKey().toString(), entry.getValue().toString());
    }
    return content;
  }
}
