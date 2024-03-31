/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

suite("test_nullsafe_eq_prune") {
    sql "SET enable_nereids_planner=true"
    sql "SET enable_fallback_to_original_planner=false"
    sql "DROP TABLE IF EXISTS table_500_undef_partitions2_keys3_properties4_distributed_by56;"
    sql """
        create table table_500_undef_partitions2_keys3_properties4_distributed_by56 (
                col_int_undef_signed int   ,
                col_date_undef_signed date   ,
                pk int,
                col_int_undef_signed2 int   ,
                col_date_undef_signed2 date   ,
                col_varchar_10__undef_signed varchar(10)   ,
                col_varchar_1024__undef_signed varchar(1024)   
                ) engine=olap
                DUPLICATE KEY(col_int_undef_signed, col_date_undef_signed, pk)
                PARTITION BY             RANGE(col_int_undef_signed, col_date_undef_signed) (
                                PARTITION p0 VALUES LESS THAN ('4', '2023-12-11'),
                                PARTITION p1 VALUES LESS THAN ('6', '2023-12-15'),
                                PARTITION p2 VALUES LESS THAN ('7', '2023-12-16'),
                                PARTITION p3 VALUES LESS THAN ('8', '2023-12-25'),
                                PARTITION p4 VALUES LESS THAN ('8', '2024-01-18'),
                                PARTITION p5 VALUES LESS THAN ('10', '2024-02-18'),
                                PARTITION p6 VALUES LESS THAN ('1147483647', '2056-12-31'),
                                PARTITION p100 VALUES LESS THAN ('2147483647', '9999-12-31')
                            )
                distributed by hash(pk) buckets 10
                properties("replication_num" = "1");
    """
    sql """
        insert into table_500_undef_partitions2_keys3_properties4_distributed_by56(pk,col_int_undef_signed,col_int_undef_signed2,col_date_undef_signed,col_date_undef_signed2,col_varchar_10__undef_signed,col_varchar_1024__undef_signed) values (0,3,null,'2024-01-17','2024-01-31','e',null),(1,2,8,'2023-12-16','2023-12-15','e','u'),(2,5,4,'2024-01-31','2025-02-18','a','e'),(3,5,6,'2025-06-18','2026-01-18',null,'y'),(4,1,1,'2024-01-17','2025-02-18','j','g'),(5,1,7,'2024-01-19','2024-02-18','y','l'),(6,8,6,'2023-12-18','2023-12-20','b','t'),(7,3,7,'2025-06-18','2023-12-13','z',null),(8,8,2,'2023-12-14','2023-12-13','m','v'),(9,3,4,'2027-01-16','2026-02-18','j','s'),(10,null,4,'2023-12-11','2027-01-16','d','b'),(11,0,null,'2025-02-18','2023-12-19','x','o'),(12,null,8,'2023-12-16','2024-01-09','j','e'),(13,9,0,'2024-02-18','2027-01-16','s','y'),(14,null,2,'2023-12-10','2026-02-18','k','y'),(15,1,null,'2026-02-18','2024-01-31','m','k'),(16,null,8,'2024-02-18','2024-02-18','v','i'),(17,0,9,'2024-02-18','2025-02-18','y','m'),(18,1,0,'2023-12-19','2024-01-19','y','o'),(19,5,6,'2026-02-18','2023-12-13','m',null),(20,null,4,'2024-02-18','2027-01-16','x','y'),(21,null,2,'2023-12-18','2024-01-09','g','s'),(22,0,0,'2024-01-08','2023-12-13','j',null),(23,0,2,'2027-01-16','2023-12-14',null,'l'),(24,2,6,'2024-02-18','2023-12-14','i','n'),(25,5,9,'2025-02-18','2023-12-14',null,'s'),(26,7,8,'2025-02-18','2024-01-19','t','a'),(27,7,null,'2024-02-18','2024-02-18','v','t'),(28,3,9,'2024-01-09','2026-01-18','b','b'),(29,null,9,'2023-12-14','2027-01-09','b','l'),(30,1,6,'2023-12-14','2023-12-11','s','f'),(31,6,0,'2024-01-09','2024-01-08',null,'u'),(32,null,8,'2023-12-18','2024-02-18',null,null),(33,4,3,'2023-12-13','2023-12-15','s','x'),(34,6,9,'2023-12-20','2024-01-31','q','z'),(35,5,1,'2023-12-12','2023-12-18','p','m'),(36,5,5,'2024-01-19','2027-01-16','i','k'),(37,9,5,'2025-06-18','2026-02-18','t','i'),(38,null,null,'2023-12-13','2026-01-18','m','d'),(39,null,9,'2023-12-13','2023-12-18','f','k'),(40,3,7,'2026-02-18','2023-12-16','g','y'),(41,3,8,'2024-01-31','2027-01-09','g','p'),(42,2,7,'2023-12-15','2024-01-17',null,'a'),(43,null,5,'2023-12-14','2025-02-18','s','n'),(44,4,4,'2023-12-15','2025-06-18','n','n'),(45,0,8,'2025-06-18','2027-01-09','i','q'),(46,3,5,'2025-06-18','2024-01-08',null,'v'),(47,6,6,'2025-06-18','2024-01-08','c','u'),(48,3,1,'2024-01-17','2025-02-18','z','f'),(49,3,null,'2023-12-12','2025-06-18','n','j'),(50,6,8,'2025-02-18','2023-12-12','u','z'),(51,1,1,'2024-02-18','2025-02-18',null,'z'),(52,7,0,'2026-02-18','2023-12-13','b','h'),(53,8,7,'2024-01-08','2023-12-14','o','k'),(54,4,7,'2026-02-18','2023-12-20','b','m'),(55,5,5,'2024-01-09','2025-06-18','h','g'),(56,6,2,'2023-12-10','2024-01-19','y','v'),(57,null,4,'2023-12-19','2024-01-09','h','g'),(58,0,7,'2025-02-18','2024-01-17',null,null),(59,6,3,'2023-12-10','2023-12-20',null,'m'),(60,7,null,'2024-02-18','2024-02-18',null,'q'),(61,9,6,'2025-02-18','2023-12-19','j','d'),(62,8,5,'2023-12-09','2025-06-18',null,'n'),(63,8,2,'2023-12-13','2023-12-19',null,'q'),(64,5,9,'2024-02-18','2023-12-09','j','p'),(65,1,2,'2024-01-08','2023-12-20','q',null),(66,1,9,'2025-02-18','2027-01-16','r','h'),(67,9,0,'2024-02-18','2023-12-10',null,'j'),(68,7,2,'2023-12-17','2023-12-13','m','u'),(69,5,0,'2023-12-17','2023-12-20','v','v'),(70,5,null,'2024-01-09','2023-12-10','i','m'),(71,2,4,'2024-01-09','2024-01-31','j','v'),(72,0,null,'2023-12-09','2023-12-16','l','z'),(73,7,2,'2025-02-18','2025-06-18',null,'t'),(74,8,5,'2023-12-19','2023-12-12','f','x'),(75,6,0,'2023-12-12','2023-12-10',null,'u'),(76,4,1,'2024-01-17','2023-12-19','b','j'),(77,2,null,'2024-01-31','2025-02-18',null,'t'),(78,3,2,'2024-01-17','2023-12-14','w','q'),(79,5,7,'2025-06-18','2023-12-20',null,'b'),(80,3,5,'2026-01-18','2023-12-18','l','e'),(81,null,4,'2024-01-31','2025-02-18','i',null),(82,2,null,'2023-12-17','2023-12-16','t','s'),(83,2,7,'2026-01-18','2024-01-19','n','e'),(84,null,3,'2023-12-11','2024-01-31','a','p'),(85,6,0,'2024-01-17','2024-02-18','w','n'),(86,0,4,'2023-12-15','2024-01-19',null,'r'),(87,5,6,'2027-01-16','2023-12-13','x','a'),(88,0,3,'2023-12-18','2023-12-15','j','c'),(89,5,2,'2025-06-18','2025-02-18','l',null),(90,8,1,'2023-12-19','2023-12-14','c','h'),(91,5,8,'2023-12-15','2023-12-16','b','d'),(92,1,4,'2025-06-18','2025-06-18','j','r'),(93,2,6,'2023-12-20','2025-06-18','u','r'),(94,0,null,'2027-01-16','2025-02-18','s','a'),(95,5,9,'2023-12-20','2025-06-18','m','c'),(96,null,4,'2023-12-11','2026-02-18','k','i'),(97,9,null,'2023-12-20','2023-12-13',null,'n'),(98,1,5,'2023-12-18','2023-12-12','x','f'),(99,4,3,'2023-12-18','2023-12-12',null,'h'),(100,1,null,'2023-12-19','2024-01-17',null,'e'),(101,2,5,'2024-01-31','2024-01-09','r','u'),(102,2,3,'2024-01-08','2024-02-18','y','v'),(103,null,2,'2023-12-16','2023-12-09','y','s'),(104,6,8,'2023-12-16','2025-02-18','i','e'),(105,null,5,'2023-12-14','2025-02-18',null,'w'),(106,null,4,'2024-02-18','2023-12-10','a',null),(107,2,null,'2023-12-13','2025-02-18','h','x'),(108,1,1,'2024-02-18','2024-01-31','o','d'),(109,3,7,'2025-06-18','2023-12-17','h','a'),(110,0,7,'2025-06-18','2023-12-17','g',null),(111,5,null,'2026-02-18','2024-01-17','a','u'),(112,1,4,'2023-12-18','2023-12-10','c','k'),(113,0,8,'2023-12-15','2024-02-18','n',null),(114,null,2,'2024-02-18','2024-01-17','h',null),(115,5,6,'2023-12-10','2027-01-16',null,'y'),(116,8,2,'2024-02-18','2024-02-18','x',null),(117,6,0,'2027-01-09','2026-01-18','w','a'),(118,2,6,'2027-01-16','2023-12-15','m','e'),(119,2,2,'2024-01-17','2024-01-09','h','e'),(120,2,9,'2024-01-09','2023-12-12','j','k'),(121,3,1,'2023-12-20','2023-12-19','p',null),(122,7,0,'2023-12-20','2025-02-18',null,'v'),(123,9,1,'2023-12-10','2023-12-10',null,'k'),(124,0,7,'2023-12-17','2025-02-18','c','z'),(125,3,5,'2026-01-18','2023-12-11','o','d'),(126,9,null,'2025-02-18','2023-12-16','v','m'),(127,6,1,'2023-12-09','2023-12-13','h','p'),(128,5,9,'2023-12-15','2024-01-09','q','k'),(129,1,7,'2023-12-15','2024-01-19','m','s'),(130,null,1,'2023-12-11','2024-02-18','h','x'),(131,0,3,'2023-12-18','2023-12-18','k','y'),(132,2,1,'2024-02-18','2025-02-18','f','d'),(133,2,7,'2023-12-19','2023-12-09','t','e'),(134,2,8,'2027-01-16','2023-12-15','f',null),(135,5,3,'2027-01-09','2027-01-09','r','u'),(136,5,6,'2023-12-11','2026-01-18',null,'a'),(137,9,0,'2027-01-16','2025-06-18','p','n'),(138,2,2,'2023-12-12','2023-12-18','y','e'),(139,8,8,'2023-12-09','2023-12-13','z','u'),(140,9,null,'2027-01-09','2023-12-15','t','q'),(141,7,5,'2023-12-09','2023-12-09','a','m'),(142,null,3,'2024-01-19','2024-02-18','g','z'),(143,6,4,'2027-01-16','2023-12-11','s',null),(144,2,null,'2025-06-18','2023-12-17','a','m'),(145,1,6,'2024-01-09','2023-12-18','i','k'),(146,3,3,'2024-01-08','2023-12-20','c','b'),(147,7,8,'2026-02-18','2025-06-18',null,'c'),(148,8,9,'2026-01-18','2023-12-11','v','g'),(149,0,9,'2025-02-18','2025-02-18','r','t'),(150,2,null,'2023-12-14','2023-12-18','k',null),(151,4,2,'2025-02-18','2024-02-18','g','f'),(152,5,8,'2023-12-16','2023-12-09','w','j'),(153,5,5,'2025-06-18','2025-06-18','q','t'),(154,3,null,'2024-01-31','2024-01-09','i','d'),(155,5,null,'2024-01-19','2023-12-11','a','u'),(156,7,3,'2023-12-17','2024-01-17','y','q'),(157,9,3,'2024-01-19','2023-12-19','l',null),(158,5,6,'2023-12-11','2025-06-18','t','u'),(159,6,7,'2023-12-19','2025-06-18','r','p'),(160,3,null,'2024-02-18','2023-12-15','g','c'),(161,6,6,'2023-12-15','2023-12-09','e','d'),(162,3,7,'2023-12-14','2023-12-12',null,'m'),(163,9,9,'2023-12-17','2023-12-10','f','q'),(164,2,0,'2023-12-18','2024-01-08',null,'b'),(165,7,5,'2024-02-18','2023-12-10','v','l'),(166,2,9,'2023-12-11','2023-12-20','l','m'),(167,7,3,'2024-02-18','2023-12-10','t','d'),(168,3,9,'2024-02-18','2023-12-15','h','y'),(169,2,7,'2024-01-08','2024-02-18','m','p'),(170,3,4,'2024-01-08','2023-12-17','j','e'),(171,3,3,'2023-12-17','2027-01-16','m','v'),(172,8,4,'2023-12-09','2025-06-18','d','q'),(173,0,0,'2026-02-18','2024-01-31','b','m'),(174,4,6,'2027-01-09','2025-02-18','f','h'),(175,1,1,'2023-12-15','2023-12-13','d','v'),(176,8,6,'2026-01-18','2024-01-09','q','c'),(177,4,3,'2024-01-19','2023-12-16','c','e'),(178,7,7,'2024-02-18','2025-06-18','r','u'),(179,2,4,'2024-01-08','2025-02-18','v',null),(180,6,2,'2023-12-18','2023-12-11','a','b'),(181,3,5,'2023-12-13','2023-12-12','w',null),(182,0,8,'2025-02-18','2026-02-18','m','i'),(183,2,9,'2027-01-16','2027-01-16','c',null),(184,null,1,'2024-01-09','2025-06-18','s','y'),(185,7,7,'2023-12-16','2023-12-09','f',null),(186,4,null,'2024-01-31','2024-01-09','v',null),(187,0,5,'2023-12-11','2025-06-18','k',null),(188,7,null,'2024-01-19','2025-02-18',null,'k'),(189,3,2,'2023-12-10','2024-01-09',null,null),(190,7,1,'2027-01-16','2023-12-11','x',null),(191,5,8,'2023-12-14','2025-06-18','f','p'),(192,null,4,'2025-02-18','2024-01-09','g','g'),(193,6,0,'2027-01-16','2025-02-18','e','a'),(194,null,2,'2023-12-13','2025-02-18','b',null),(195,9,1,'2023-12-18','2023-12-16','r','c'),(196,2,0,'2027-01-09','2023-12-16','t','t'),(197,null,4,'2024-01-09','2024-02-18','z','i'),(198,null,7,'2026-02-18','2027-01-16','l',null),(199,9,4,'2025-02-18','2023-12-19','o','p'),(200,3,1,'2023-12-12','2023-12-18','k','s'),(201,9,null,'2025-06-18','2024-01-19','j','f'),(202,4,9,'2025-06-18','2023-12-20','o','w'),(203,4,5,'2023-12-19','2026-01-18','v','o'),(204,9,2,'2023-12-10','2025-02-18','r','j'),(205,8,5,'2026-02-18','2024-01-17','i','l'),(206,2,9,'2024-01-09','2026-01-18','z',null),(207,2,5,'2023-12-11','2023-12-09','r','o'),(208,0,6,'2025-06-18','2025-02-18','d','g'),(209,6,0,'2024-01-31','2023-12-15','o',null),(210,5,null,'2023-12-13','2023-12-17','e','n'),(211,9,2,'2023-12-19','2023-12-09','j','i'),(212,7,9,'2027-01-16','2023-12-09',null,'r'),(213,8,8,'2024-02-18','2023-12-13','j','m'),(214,9,0,'2025-02-18','2023-12-14','t','m'),(215,4,2,'2025-02-18','2023-12-20',null,'b'),(216,null,9,'2024-01-17','2024-02-18','g','g'),(217,4,4,'2023-12-18','2023-12-16','t','f'),(218,2,0,'2023-12-18','2024-01-08','e','c'),
        (219,1,4,'2024-02-18','2026-01-18','l','l'),(220,9,null,'2027-01-16','2023-12-13','y','a'),(221,null,8,'2023-12-13','2024-02-18','k','p'),(222,1,2,'2023-12-19','2026-01-18',null,'p'),(223,null,2,'2023-12-15','2024-01-09','r','z'),(224,9,5,'2025-06-18','2023-12-20','t','t'),(225,0,5,'2023-12-20','2023-12-20','p','t'),(226,4,null,'2025-02-18','2024-01-31','o','w'),(227,6,9,'2023-12-17','2024-02-18',null,'m'),(228,0,5,'2025-02-18','2024-02-18','b','g'),(229,4,8,'2023-12-16','2024-01-17','o','i'),(230,null,7,'2024-02-18','2025-02-18','c','i'),(231,4,5,'2024-02-18','2023-12-17','n',null),(232,9,2,'2025-06-18','2023-12-09','f',null),(233,null,7,'2023-12-13','2024-02-18',null,'x'),(234,null,9,'2024-01-08','2024-01-17','a','t'),(235,1,7,'2025-06-18','2023-12-11','q',null),(236,8,1,'2023-12-19','2023-12-18','o','m'),(237,null,9,'2025-06-18','2023-12-13','w',null),(238,7,5,'2024-01-19','2023-12-15','z','b'),(239,1,2,'2027-01-09','2024-02-18',null,'m'),(240,null,7,'2024-01-19','2023-12-12','f',null),(241,0,4,'2024-01-31','2024-01-09','e','c'),(242,null,2,'2025-02-18','2024-01-08','l','x'),(243,3,6,'2023-12-15','2023-12-19','w','q'),(244,8,1,'2023-12-14','2024-02-18','a','k'),(245,9,5,'2025-02-18','2024-01-19','c','z'),(246,null,6,'2023-12-10','2025-02-18',null,'v'),(247,2,null,'2026-01-18','2025-06-18','p','k'),(248,3,4,'2025-02-18','2025-06-18',null,'w'),(249,8,3,'2023-12-15','2023-12-09','i','v'),(250,6,2,'2025-02-18','2023-12-13','w',null),(251,5,7,'2025-06-18','2024-01-17','p','k'),(252,2,1,'2023-12-13','2027-01-16','d','j'),(253,6,3,'2025-02-18','2023-12-19','x','j'),(254,8,9,'2026-01-18','2024-01-31','x','z'),(255,5,9,'2024-02-18','2025-02-18','h','f'),(256,1,1,'2024-01-08','2024-01-08','g',null),(257,null,6,'2027-01-16','2027-01-09','v',null),(258,6,1,'2024-02-18','2024-02-18','q','d'),(259,4,7,'2027-01-16','2025-02-18','j','t'),(260,9,4,'2024-02-18','2023-12-12','x','x'),(261,9,9,'2023-12-17','2024-01-17',null,'t'),(262,6,0,'2024-01-17','2023-12-15',null,'n'),(263,1,9,'2024-01-09','2024-01-19','z','q'),(264,8,null,'2023-12-16','2024-02-18','d','d'),(265,4,null,'2024-01-09','2025-06-18','f','g'),(266,8,2,'2024-01-17','2023-12-13','y','v'),(267,null,6,'2027-01-16','2024-01-31','p','b'),(268,null,7,'2023-12-15','2023-12-20','o','y'),(269,null,null,'2026-02-18','2023-12-20','b','d'),(270,9,7,'2023-12-11','2023-12-11',null,'e'),(271,2,6,'2024-02-18','2027-01-16','c','a'),(272,2,2,'2023-12-15','2023-12-16','g','d'),(273,1,null,'2023-12-11','2023-12-15','c',null),(274,0,9,'2024-02-18','2023-12-16',null,'s'),(275,null,1,'2024-02-18','2023-12-14','z','i'),(276,null,7,'2025-06-18','2024-02-18','p','u'),(277,4,5,'2025-02-18','2023-12-11','i',null),(278,null,5,'2023-12-09','2023-12-11','g','x'),(279,1,4,'2024-02-18','2023-12-19','a','k'),(280,null,2,'2023-12-15','2023-12-14','g','z'),(281,7,2,'2024-02-18','2023-12-15','f','c'),(282,5,1,'2023-12-19','2023-12-14','a','k'),(283,5,0,'2023-12-11','2024-02-18','w','h'),(284,3,3,'2025-02-18','2023-12-20','x','e'),(285,4,4,'2023-12-17','2023-12-11','c','p'),(286,9,8,'2024-02-18','2024-01-08','g','y'),(287,2,2,'2024-01-31','2023-12-19','l','t'),(288,5,0,'2023-12-16','2023-12-16','e','k'),(289,null,8,'2025-02-18','2024-01-31','m','b'),(290,null,7,'2024-01-19','2023-12-17','e','p'),(291,null,7,'2027-01-09','2023-12-10','p','h'),(292,7,0,'2023-12-12','2024-02-18','f','p'),(293,5,2,'2023-12-17','2027-01-16','t','t'),(294,6,null,'2023-12-18','2025-06-18','o','t'),(295,3,1,'2024-02-18','2023-12-14','l',null),(296,9,4,'2024-01-09','2024-01-19',null,'e'),(297,1,5,'2023-12-12','2023-12-10','v','l'),(298,0,9,'2023-12-20','2023-12-09','j','q'),(299,null,9,'2024-01-31','2024-01-31',null,'m'),(300,7,3,'2023-12-11','2024-02-18','y','s'),(301,7,1,'2026-01-18','2023-12-16','s','c'),(302,8,2,'2024-01-08','2026-01-18','x','h'),(303,null,6,'2024-01-17','2025-06-18','f','u'),(304,4,5,'2026-02-18','2023-12-09','z','f'),(305,5,5,'2024-01-19','2023-12-16','g','s'),(306,2,9,'2024-01-09','2023-12-15','s','u'),(307,0,3,'2023-12-17','2024-02-18','j',null),(308,5,1,'2023-12-18','2024-01-17','v','l'),(309,null,2,'2025-02-18','2023-12-09','r',null),(310,6,5,'2023-12-19','2024-02-18','a','h'),(311,8,6,'2023-12-09','2024-01-17','u','j'),(312,0,null,'2023-12-16','2023-12-16','f','a'),(313,1,1,'2023-12-13','2023-12-14','x','a'),(314,7,1,'2027-01-09','2024-01-09','w','g'),(315,7,2,'2025-02-18','2025-06-18','q',null),(316,6,null,'2025-06-18','2023-12-13','w',null),(317,8,null,'2026-01-18','2023-12-12','c','e'),(318,null,5,'2025-02-18','2027-01-16','k','d'),(319,null,1,'2027-01-16','2024-02-18',null,'a'),(320,1,1,'2026-02-18','2025-06-18',null,'t'),(321,6,5,'2024-01-19','2023-12-20','s','h'),(322,7,3,'2023-12-14','2025-02-18','w',null),(323,8,7,'2024-02-18','2025-02-18',null,'v'),(324,9,4,'2023-12-17','2025-06-18','b','l'),(325,8,null,'2024-02-18','2025-02-18','u','s'),(326,5,0,'2024-01-19','2023-12-20','j','w'),(327,8,1,'2026-02-18','2027-01-09','f','g'),(328,2,null,'2025-02-18','2023-12-11','c','u'),(329,2,5,'2023-12-10','2024-01-31','i','m'),(330,5,9,'2023-12-19','2023-12-10','h','i'),(331,6,4,'2024-02-18','2024-01-31','r',null),(332,null,9,'2023-12-13','2023-12-16','f','n'),(333,null,8,'2023-12-17','2025-06-18','x','j'),(334,7,1,'2027-01-09','2024-02-18','k','g'),(335,8,2,'2025-02-18','2023-12-20','n','t'),(336,4,1,'2023-12-16','2024-02-18','m','r'),(337,9,2,'2025-02-18','2023-12-20','f','q'),(338,2,3,'2023-12-15','2024-02-18','b','j'),(339,null,8,'2023-12-19','2025-06-18','y',null),(340,1,9,'2025-06-18','2024-01-19','m','l'),(341,3,6,'2023-12-15','2025-06-18',null,'i'),(342,1,6,'2025-06-18','2023-12-10','q','p'),(343,0,7,'2026-01-18','2025-06-18','f',null),(344,3,9,'2023-12-12','2025-02-18','u','w'),(345,1,5,'2024-01-19','2024-01-31','x','q'),(346,5,null,'2024-01-19','2024-01-19','j','r'),(347,9,1,'2023-12-18','2023-12-19','s','y'),(348,9,1,'2023-12-11','2023-12-10',null,null),(349,9,9,'2023-12-10','2024-02-18','x',null),(350,4,8,'2025-02-18','2023-12-17','u',null),(351,null,3,'2024-02-18','2024-01-17',null,'g'),(352,null,6,'2023-12-20','2025-06-18','z','n'),(353,8,null,'2023-12-18','2023-12-18','c','k'),(354,0,7,'2024-01-31','2023-12-12','z','d'),(355,7,6,'2026-02-18','2023-12-11','m','j'),(356,4,null,'2023-12-13','2023-12-12','w','j'),(357,2,3,'2024-01-08','2026-02-18','o',null),(358,4,1,'2025-02-18','2027-01-09','x','f'),(359,2,2,'2023-12-20','2023-12-16',null,'r'),(360,null,1,'2025-06-18','2023-12-11','c','g'),(361,4,9,'2027-01-09','2023-12-14','p','v'),(362,9,8,'2027-01-16','2024-01-09','m','d'),(363,6,4,'2024-01-08','2026-02-18','w','a'),(364,3,9,'2023-12-13','2023-12-16','a','k'),(365,null,7,'2024-01-19','2024-01-09','e','s'),(366,5,null,'2026-01-18','2023-12-11','a','b'),(367,9,2,'2023-12-11','2023-12-11','j','c'),(368,0,0,'2023-12-20','2023-12-20','f','a'),(369,7,5,'2023-12-09','2023-12-10','l','b'),(370,0,4,'2026-01-18','2023-12-09',null,'j'),(371,null,6,'2025-02-18','2023-12-14','o',null),(372,3,8,'2024-01-08','2024-02-18','h','h'),(373,8,9,'2024-02-18','2025-02-18','h','n'),(374,0,7,'2023-12-15','2026-02-18','h','u'),(375,5,9,'2023-12-15','2023-12-14','a',null),(376,5,1,'2023-12-10','2023-12-18','e','n'),(377,1,3,'2023-12-10','2023-12-20','t','l'),(378,null,2,'2027-01-16','2024-01-19','a','l'),(379,5,1,'2027-01-16','2023-12-09','v','q'),(380,6,2,'2023-12-19','2025-06-18','t','u'),(381,0,null,'2023-12-12','2025-06-18','n',null),(382,0,2,'2023-12-16','2026-02-18','a','s'),(383,7,6,'2026-02-18','2024-01-31','n','b'),(384,7,0,'2025-06-18','2023-12-19','a','b'),(385,4,0,'2024-01-09','2023-12-20','n','h'),(386,0,7,'2023-12-12','2023-12-19','p',null),(387,6,6,'2023-12-16','2023-12-14','f','f'),(388,1,9,'2024-01-17','2024-01-31','i','w'),(389,2,2,'2023-12-18','2024-01-09','a','q'),(390,null,7,'2024-02-18','2025-06-18','w','f'),(391,null,4,'2023-12-11','2023-12-11','x','s'),(392,8,4,'2023-12-19','2027-01-09','y','a'),(393,null,5,'2026-02-18','2023-12-11',null,'d'),(394,1,null,'2023-12-13','2023-12-12','h','h'),(395,4,null,'2023-12-12','2023-12-11',null,'n'),(396,1,2,'2023-12-09','2024-01-09','z','f'),(397,8,8,'2023-12-18','2025-06-18','w',null),(398,9,2,'2023-12-15','2025-06-18',null,'v'),(399,5,5,'2024-01-17','2023-12-18','g','t'),(400,null,0,'2023-12-19','2023-12-20','b','m'),(401,0,1,'2023-12-12','2024-01-09','a','i'),(402,6,5,'2023-12-15','2024-02-18','e','w'),(403,null,6,'2025-02-18','2026-02-18','r',null),(404,4,0,'2023-12-11','2026-01-18','f','a'),(405,8,1,'2023-12-15','2023-12-18','d','l'),(406,3,null,'2025-06-18','2023-12-12','c','q'),(407,7,6,'2025-02-18','2024-02-18','s',null),(408,5,0,'2024-02-18','2024-02-18','i','l'),(409,3,0,'2025-02-18','2024-02-18','k','s'),(410,2,3,'2026-02-18','2024-02-18','y','c'),(411,7,5,'2023-12-10','2023-12-11','p','q'),(412,5,3,'2027-01-16','2024-01-08','k','h'),(413,null,null,'2025-06-18','2023-12-17',null,'o'),(414,4,7,'2026-01-18','2026-01-18','o','x'),(415,9,6,'2027-01-09','2023-12-14','i','o'),(416,0,4,'2023-12-19','2024-01-17','e','j'),(417,9,null,'2023-12-12','2027-01-16','y','r'),(418,null,3,'2023-12-13','2023-12-18',null,'t'),(419,7,7,'2023-12-17','2024-02-18','q','u'),(420,null,0,'2023-12-20','2025-02-18','y',null),(421,2,7,'2023-12-19','2023-12-14','w','t'),(422,0,5,'2025-06-18','2024-02-18','o',null),(423,8,6,'2023-12-18','2024-01-08','c',null),(424,null,3,'2025-02-18','2023-12-13','d','n'),(425,9,0,'2023-12-15','2023-12-18','v','b'),(426,1,null,'2027-01-16','2023-12-16','t',null),(427,null,6,'2024-02-18','2024-01-09','c','i'),(428,9,null,'2023-12-09','2024-01-09','c',null),(429,7,9,'2023-12-15','2025-02-18','n','n'),(430,4,6,'2023-12-13','2025-02-18','q','s'),(431,6,6,'2024-02-18','2023-12-14','q','w'),(432,null,4,'2024-02-18','2024-02-18',null,'z'),(433,4,null,'2024-02-18','2027-01-16','u','z'),(434,4,9,'2023-12-12','2023-12-18','r','d'),(435,7,null,'2025-02-18','2024-02-18','b','k'),(436,8,8,'2023-12-17','2023-12-13',null,'x'),(437,6,2,'2027-01-16','2026-02-18','x','j'),(438,9,0,'2024-01-08','2024-01-08','f','h'),(439,8,6,'2025-06-18','2024-02-18',null,'c'),(440,8,0,'2024-02-18','2025-02-18',null,'p'),(441,9,1,'2024-02-18','2025-02-18',null,'q'),(442,null,9,'2025-02-18','2023-12-12','m','e'),(443,0,8,'2023-12-18','2023-12-20','f','h'),(444,null,3,'2023-12-11','2023-12-13','q','r'),(445,5,9,'2023-12-18','2024-01-31','l','p'),(446,3,2,'2023-12-17','2024-02-18','d','q'),(447,0,4,'2023-12-16','2024-01-31','g','q'),(448,6,4,'2023-12-14','2027-01-09','j','g'),(449,1,null,'2023-12-16','2025-02-18','z','u'),(450,8,6,'2025-02-18','2023-12-12','o','q'),(451,2,7,'2024-01-19','2023-12-10','h',null),(452,1,null,'2026-02-18','2024-02-18','d','h'),
        (453,7,8,'2027-01-16','2025-02-18','q','t'),(454,6,3,'2023-12-17','2025-06-18',null,null),(455,5,8,'2023-12-17','2025-06-18','j','i'),(456,4,3,'2023-12-09','2024-02-18',null,'p'),(457,1,8,'2023-12-17','2027-01-16','n','r'),(458,2,1,'2023-12-09','2023-12-10',null,'b'),(459,5,8,'2024-01-19','2023-12-10','r','j'),(460,3,6,'2024-02-18','2025-02-18','g','g'),(461,2,7,'2026-01-18','2023-12-16',null,'e'),(462,2,1,'2024-02-18','2023-12-16','k',null),(463,9,8,'2023-12-12','2024-01-31','j','t'),(464,5,3,'2024-02-18','2024-02-18','f','d'),(465,4,1,'2023-12-18','2023-12-14','d','f'),(466,null,6,'2026-01-18','2023-12-12','m',null),(467,7,9,'2024-01-19','2024-01-19',null,'c'),(468,6,null,'2025-02-18','2025-06-18','i','h'),(469,2,5,'2023-12-13','2023-12-11','q','g'),(470,9,0,'2023-12-14','2023-12-19','x',null),(471,2,null,'2023-12-14','2025-02-18','i','p'),(472,5,9,'2023-12-11','2023-12-16','n','q'),(473,null,4,'2025-02-18','2024-01-08',null,'u'),(474,0,7,'2024-01-17','2027-01-16','f','o'),(475,4,6,'2025-06-18','2023-12-17','a','h'),(476,8,7,'2024-02-18','2027-01-16','i',null),(477,1,7,'2024-01-09','2026-02-18','v','h'),(478,1,0,'2024-02-18','2023-12-17',null,'d'),(479,7,5,'2023-12-15','2024-02-18','c','z'),(480,3,9,'2024-02-18','2024-02-18','o','c'),(481,2,6,'2024-02-18','2025-02-18','k','n'),(482,8,9,'2023-12-17','2023-12-15','v',null),(483,null,6,'2027-01-16','2023-12-15','x','y'),(484,7,6,'2025-02-18','2023-12-16','l','k'),(485,4,6,'2023-12-11','2027-01-09',null,'o'),(486,null,2,'2024-02-18','2024-01-17','w','q'),(487,4,null,'2023-12-19','2024-02-18','u','k'),(488,9,0,'2024-02-18','2024-01-17','t','a'),(489,5,3,'2024-02-18','2024-02-18',null,'h'),(490,null,null,'2023-12-11','2024-02-18','a','c'),(491,0,4,'2024-01-31','2023-12-12','y','e'),(492,2,1,'2025-02-18','2023-12-13','h','g'),(493,3,1,'2023-12-14','2023-12-12','s','b'),(494,null,3,'2023-12-13','2023-12-13','x','w'),(495,9,0,'2025-02-18','2023-12-20','y','w'),(496,3,1,'2024-02-18','2023-12-11','q','l'),(497,null,0,'2024-01-17','2027-01-09','i','q'),(498,3,null,'2025-02-18','2025-02-18','w','p'),(499,null,7,'2025-02-18','2023-12-19','h','t');
    """
    qt_select """ SELECT count(*)
                FROM table_500_undef_partitions2_keys3_properties4_distributed_by56 AS table1
                WHERE NOT (  table1 . `col_date_undef_signed` <=> '2024-01-19' ) ;"""
}
