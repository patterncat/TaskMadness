/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.spring.marchmadness.enricher;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Michael Minella
 */
public class BracketScoringTraversalCallbackFactory {

	private static final String USA_TODAY_STATS = "select name, rating from NCAA_STATS where year = 2016 order by rating desc";
	private static final String MOORE_STATS = "select name, pr from MOORE_NCAA_STATS where year = 2016 order by pr desc";

	private JdbcOperations jdbcTemplate;

	private final Map<String, Long> rankings = new HashMap<>();

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public BracketScoringTraversalCallback getObject() {

		final Map<String, Long> usaTodayRankings = new HashMap<>();
		final Map<String, Long> mooreRankings = new HashMap<>();

		if(rankings.isEmpty()) {
			jdbcTemplate.query(USA_TODAY_STATS, (rs, rowNum) -> {
				usaTodayRankings.put(rs.getString("name"), rs.getLong("rating"));
				return null;
			});
			jdbcTemplate.query(MOORE_STATS, (rs, rowNum) -> {
				mooreRankings.put(rs.getString("name"), rs.getLong("pr"));
				return null;
			});

			for (Map.Entry<String, Long> teamRanking : usaTodayRankings.entrySet()) {

				String teamName = teamRanking.getKey();

				if(mooreRankings.containsKey(teamName)) {
					rankings.put(teamName, (teamRanking.getValue() * 7) + (mooreRankings.get(teamName) * 3));
				}
				else {
					System.out.println(">> Missing name from Moore: " + teamName);
				}

			}
		}

		BracketScoringTraversalCallback callback = new BracketScoringTraversalCallback(rankings);

		return callback;
	}
}
