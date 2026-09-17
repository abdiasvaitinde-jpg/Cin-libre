package com.example.data.model

import com.example.R

object MediaCatalog {
    val items: List<MediaItem> = listOf(
        MediaItem(
            id = "cosmos_odyssey",
            title = "L'Odyssée Cosmique",
            type = MediaType.MOVIE,
            synopsis = "Une expédition spatiale sans précédent aux confins de la galaxie pour sauver l'humanité de l'extinction. Une œuvre visuelle époustouflante explorant les trous noirs et les mystères de l'espace profond.",
            category = "Sci-Fi",
            duration = "12m 14s",
            releaseYear = 2024,
            rating = 4.9f,
            videoUrl = "https://archive.org/download/Tears-of-Steel/tears_of_steel_720p.mp4",
            downloadFileSizeMb = 76,
            localDrawableRes = R.drawable.img_hero_cosmos,
            thumbnailUrl = "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?w=600&auto=format&fit=crop&q=80",
            director = "Ian Hubert & Blender Studio",
            quality = "720p HD"
        ),
        MediaItem(
            id = "sintel_quest",
            title = "Sintel : La Quête du Dragon",
            type = MediaType.MOVIE,
            synopsis = "Dans un univers fantastique impitoyable, une jeune guerrière solitaire parcourt le monde pour retrouver son ami Scales, un bébé dragon enlevé par une créature ailée géante.",
            category = "Animation & Fantastique",
            duration = "15m",
            releaseYear = 2023,
            rating = 4.8f,
            videoUrl = "https://test-videos.co.uk/vids/sintel/mp4/h264/720/Sintel_720_10s_1MB.mp4",
            downloadFileSizeMb = 12,
            localDrawableRes = R.drawable.img_hero_animation,
            thumbnailUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=600&auto=format&fit=crop&q=80",
            director = "Colin Levy",
            quality = "720p HD"
        ),
        MediaItem(
            id = "bunny_adventure",
            title = "Le Grand Lièvre & la Forêt",
            type = MediaType.MOVIE,
            synopsis = "Un paisible lapin géant voit sa tranquillité troublée par trois rongeurs espiègles qui maltraitent les animaux de la forêt. Il décide de préparer un plan malicieux pour leur donner une leçon inoubliable.",
            category = "Animation & Famille",
            duration = "10m",
            releaseYear = 2024,
            rating = 4.7f,
            videoUrl = "https://raw.githubusercontent.com/mediaelement/mediaelement-files/master/big_buck_bunny.mp4",
            downloadFileSizeMb = 10,
            thumbnailUrl = "https://images.unsplash.com/photo-1535083783855-76ae62b2914e?w=600&auto=format&fit=crop&q=80",
            director = "Sacha Goedegebure",
            quality = "1080p Full HD"
        ),
        MediaItem(
            id = "cyber_city_series",
            title = "Cyber City 2099",
            type = MediaType.SERIES,
            synopsis = "Dans une mégalopole cyberpunk sous surveillance constante, une équipe de hackers rebelles combat des corporations toutes-puissantes pour libérer la vérité technologique.",
            category = "Série Sci-Fi",
            duration = "Saison 1 (3 Épisodes)",
            releaseYear = 2024,
            rating = 4.9f,
            videoUrl = "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/720/Big_Buck_Bunny_720_10s_1MB.mp4",
            downloadFileSizeMb = 15,
            thumbnailUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600&auto=format&fit=crop&q=80",
            director = "Alexandre Vane",
            quality = "720p HD",
            episodes = listOf(
                Episode(
                    id = "cyber_ep_1",
                    seriesId = "cyber_city_series",
                    episodeNumber = 1,
                    title = "Épisode 1 : Le Réveil du Réseau",
                    duration = "10s",
                    synopsis = "Une faille inattendue dans le superordinateur central révèle une présence clandestine qui pirate les serveurs de la ville.",
                    videoUrl = "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/720/Big_Buck_Bunny_720_10s_1MB.mp4",
                    downloadFileSizeMb = 5
                ),
                Episode(
                    id = "cyber_ep_2",
                    seriesId = "cyber_city_series",
                    episodeNumber = 2,
                    title = "Épisode 2 : L'Embrasement Silencieux",
                    duration = "10s",
                    synopsis = "Les patrouilles de sécurité traquent les rebelles dans les sous-sols industriels du secteur néon.",
                    videoUrl = "https://test-videos.co.uk/vids/sintel/mp4/h264/720/Sintel_720_10s_1MB.mp4",
                    downloadFileSizeMb = 5
                ),
                Episode(
                    id = "cyber_ep_3",
                    seriesId = "cyber_city_series",
                    episodeNumber = 3,
                    title = "Épisode 3 : Protocole Liberté",
                    duration = "10s",
                    synopsis = "L'affrontement final sur le toit de la tour cybernétique pour diffuser la clé de décryptage.",
                    videoUrl = "https://storage.googleapis.com/exoplayer-test-media-1/mp4/android-screens-10s.mp4",
                    downloadFileSizeMb = 5
                )
            )
        ),
        MediaItem(
            id = "wild_earth_series",
            title = "Planète Sauvage : Mystères Océaniques",
            type = MediaType.SERIES,
            synopsis = "Une série documentaire immersive plongeant dans les abysses inexplorées de nos océans, révélant des créatures bioluminescentes fascinantes et des récifs protégés.",
            category = "Documentaire",
            duration = "Saison 1 (2 Épisodes)",
            releaseYear = 2023,
            rating = 4.8f,
            videoUrl = "https://test-videos.co.uk/vids/jellyfish/mp4/h264/720/Jellyfish_720_10s_1MB.mp4",
            downloadFileSizeMb = 12,
            thumbnailUrl = "https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=600&auto=format&fit=crop&q=80",
            director = "Claire Delacroix",
            quality = "720p HD",
            episodes = listOf(
                Episode(
                    id = "ocean_ep_1",
                    seriesId = "wild_earth_series",
                    episodeNumber = 1,
                    title = "Épisode 1 : Les Récifs de Corail Géants",
                    duration = "10s",
                    synopsis = "Exploration des oasis sous-marines et de la chaîne trophique des eaux tropicales turquoises.",
                    videoUrl = "https://test-videos.co.uk/vids/jellyfish/mp4/h264/720/Jellyfish_720_10s_1MB.mp4",
                    downloadFileSizeMb = 6
                ),
                Episode(
                    id = "ocean_ep_2",
                    seriesId = "wild_earth_series",
                    episodeNumber = 2,
                    title = "Épisode 2 : L'Énigme des Fosses Abyssales",
                    duration = "10s",
                    synopsis = "Descente à 6 000 mètres de profondeur où la vie prospère sans la moindre lueur du soleil.",
                    videoUrl = "https://storage.googleapis.com/exoplayer-test-media-1/mp4/android-screens-10s.mp4",
                    downloadFileSizeMb = 6
                )
            )
        ),
        MediaItem(
            id = "elephants_dream",
            title = "Le Labyrinthe Mécanique",
            type = MediaType.MOVIE,
            synopsis = "Proog et Emo évoluent au sein d'une immense machine vivante et mécanique. Mais leurs visions de ce monde s'opposent, provoquant une distorsion de la réalité.",
            category = "Animation & Sci-Fi",
            duration = "11m",
            releaseYear = 2023,
            rating = 4.6f,
            videoUrl = "https://archive.org/download/ElephantsDream/ed_1024_512kb.mp4",
            downloadFileSizeMb = 47,
            thumbnailUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=600&auto=format&fit=crop&q=80",
            director = "Bassam Kurdali",
            quality = "720p HD"
        ),
        MediaItem(
            id = "sublime_nature",
            title = "Horizons Sauvages : Les Grands Sommets",
            type = MediaType.SHORT,
            synopsis = "Un voyage contemplatif en très haute définition au sommet des chaînes de montagnes les plus majestueuses du monde, filmé par drone sous les aurores boréales.",
            category = "Documentaire",
            duration = "10s",
            releaseYear = 2024,
            rating = 4.9f,
            videoUrl = "https://test-videos.co.uk/vids/jellyfish/mp4/h264/720/Jellyfish_720_10s_1MB.mp4",
            downloadFileSizeMb = 5,
            thumbnailUrl = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=600&auto=format&fit=crop&q=80",
            director = "Lucas Moreau",
            quality = "720p HD"
        ),
        MediaItem(
            id = "the_rookie_series",
            title = "The Rookie : Le flic de Los Angeles",
            type = MediaType.SERIES,
            synopsis = "À 45 ans, John Nolan quitte sa petite ville natale après un incident bouleversant pour réaliser son rêve : devenir policier au sein du prestigieux LAPD. Accueilli avec scepticisme par sa hiérarchie qui le considère comme une crise de la quarantaine ambulante, il doit redoubler d'efforts face aux criminels les plus dangereux de Los Angeles.",
            category = "Séries",
            duration = "6 Saisons (100+ Épisodes)",
            releaseYear = 2024,
            rating = 4.9f,
            videoUrl = "https://storage.googleapis.com/exoplayer-test-media-1/mp4/android-screens-10s.mp4",
            downloadFileSizeMb = 25,
            thumbnailUrl = "https://images.unsplash.com/photo-1541872703-74c5e44368f9?w=600&auto=format&fit=crop&q=80",
            director = "Alexi Hawley • Avec Nathan Fillion, Alyssa Diaz, Richard T. Jones",
            quality = "1080p Full HD",
            episodes = listOf(
                // Saison 1
                Episode(
                    id = "rookie_s1_e1",
                    seriesId = "the_rookie_series",
                    episodeNumber = 1,
                    title = "S01E01 : Nouveau départ",
                    duration = "43 min",
                    synopsis = "John Nolan arrive à la division Mid-Wilshire de Los Angeles en tant que plus vieille recrue du LAPD sous le tutorat rigoureux de Talia Bishop.",
                    videoUrl = "https://storage.googleapis.com/exoplayer-test-media-1/mp4/android-screens-10s.mp4",
                    downloadFileSizeMb = 24
                ),
                Episode(
                    id = "rookie_s1_e2",
                    seriesId = "the_rookie_series",
                    episodeNumber = 2,
                    title = "S01E02 : Baptême du feu",
                    duration = "42 min",
                    synopsis = "Nolan doit prouver son sang-froid lorsqu'une prise d'otage imprévue menace des civils dans un centre commercial.",
                    videoUrl = "https://archive.org/download/Tears-of-Steel/tears_of_steel_720p.mp4",
                    downloadFileSizeMb = 28
                ),
                // Saison 2
                Episode(
                    id = "rookie_s2_e1",
                    seriesId = "the_rookie_series",
                    episodeNumber = 3,
                    title = "S02E01 : Les conséquences",
                    duration = "44 min",
                    synopsis = "À la suite de l'attaque bioterroriste, Nolan découvre son nouvel officier instructeur, la redoutable détective Nyla Harper.",
                    videoUrl = "https://raw.githubusercontent.com/mediaelement/mediaelement-files/master/big_buck_bunny.mp4",
                    downloadFileSizeMb = 25
                ),
                Episode(
                    id = "rookie_s2_e2",
                    seriesId = "the_rookie_series",
                    episodeNumber = 4,
                    title = "S02E02 : Le pari",
                    duration = "43 min",
                    synopsis = "Nolan et Harper traquent un réseau de faux-monnayeurs à travers les bas-fonds de Los Angeles.",
                    videoUrl = "https://test-videos.co.uk/vids/sintel/mp4/h264/720/Sintel_720_10s_1MB.mp4",
                    downloadFileSizeMb = 22
                ),
                // Saison 3
                Episode(
                    id = "rookie_s3_e1",
                    seriesId = "the_rookie_series",
                    episodeNumber = 5,
                    title = "S03E01 : Conséquences",
                    duration = "43 min",
                    synopsis = "Nolan approche de la fin de sa formation de recrue, mais un piège tendu par son supérieur Armstrong menace son avenir.",
                    videoUrl = "https://archive.org/download/Tears-of-Steel/tears_of_steel_720p.mp4",
                    downloadFileSizeMb = 26
                ),
                // Saison 4
                Episode(
                    id = "rookie_s4_e1",
                    seriesId = "the_rookie_series",
                    episodeNumber = 6,
                    title = "S04E01 : Face au cartel",
                    duration = "45 min",
                    synopsis = "L'équipe s'envole au Guatemala pour secourir Angela Lopez enlevée par la cheffe de cartel La Fiera.",
                    videoUrl = "https://storage.googleapis.com/exoplayer-test-media-1/mp4/android-screens-10s.mp4",
                    downloadFileSizeMb = 29
                ),
                // Saison 5
                Episode(
                    id = "rookie_s5_e1",
                    seriesId = "the_rookie_series",
                    episodeNumber = 7,
                    title = "S05E01 : Double jeu",
                    duration = "44 min",
                    synopsis = "Nolan devient officiellement officier instructeur (TO) et accueille sa propre recrue, Celina Juarez.",
                    videoUrl = "https://raw.githubusercontent.com/mediaelement/mediaelement-files/master/big_buck_bunny.mp4",
                    downloadFileSizeMb = 25
                ),
                // Saison 6
                Episode(
                    id = "rookie_s6_e1",
                    seriesId = "the_rookie_series",
                    episodeNumber = 8,
                    title = "S06E01 : Réplique sanglante (100e Épisode)",
                    duration = "45 min",
                    synopsis = "Après les attaques coordonnées visant le commissariat de Mid-Wilshire, Nolan et Bailey préparent leur mariage sous haute tension.",
                    videoUrl = "https://archive.org/download/Tears-of-Steel/tears_of_steel_720p.mp4",
                    downloadFileSizeMb = 30
                )
            )
        )
    )

    val categories = listOf("Tout", "Films", "Séries", "Animation & Famille", "Sci-Fi", "Documentaire")
}
