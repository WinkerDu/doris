SELECT cast(v["payload"]["comment"]["body"] as string) as comment_body, count() FROM github_events WHERE cast(v["payload"]["comment"]["body"] as string) != "" AND length(cast(v["payload"]["comment"]["body"] as string)) < 100 GROUP BY comment_body  ORDER BY count(), 1 DESC LIMIT 50