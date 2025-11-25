-- Tables are dropped and created each time project runs.
-- ! Manually run this after startup

-- Course Packages
INSERT INTO course_packages (package_id, name, description, year, semester, level) VALUES
(1, 'Package 1', 'Algorithms and Data Structures Package', 1, 1, 'Software Engineering'),
(2, 'Package 2', 'Artificial Intelligence Package', 1, 1, 'Software Engineering'),
(3, 'Package 3', 'Software Engineering Package', 1, 1, 'Software Engineering');

-- Optional Courses for Package 1
INSERT INTO optional_courses (course_id, name, code, max_students, package_id) VALUES
(101, 'Advanced Algorithms', 'CS301', 30, 1),
(102, 'Data Mining', 'CS302', 25, 1),
(103, 'Computational Geometry', 'CS303', 20, 1);

-- Optional Courses for Package 2
INSERT INTO optional_courses (course_id, name, code, max_students, package_id) VALUES
(201, 'Machine Learning', 'AI301', 28, 2),
(202, 'Neural Networks', 'AI302', 22, 2),
(203, 'Computer Vision', 'AI303', 24, 2);

-- Optional Courses for Package 3
INSERT INTO optional_courses (course_id, name, code, max_students, package_id) VALUES
(301, 'Software Architecture', 'SE301', 32, 3),
(302, 'DevOps Practices', 'SE302', 26, 3),
(303, 'Quality Assurance', 'SE303', 28, 3);
