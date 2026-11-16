const API = "/api/students";

// Display messages safely
function showMessage(elementId, message, success = true) {
    const element = document.getElementById(elementId);
    element.textContent = message;
    element.style.color = success ? "green" : "red";
}

// API request helper
async function apiRequest(url, options = {}) {
    const response = await fetch(url, options);

    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || `Request failed: ${response.status}`);
    }

    const text = await response.text();

    if (!text) return null;

    try {
        return JSON.parse(text);
    } catch {
        return text;
    }
}

// Load all students
async function loadStudents() {
    try {
        const students = await apiRequest(API);

        displayStudents(students);

        document.getElementById("totalStudents").textContent = students.length;

        const departments = new Set(
            students.map(student => student.department)
        );

        document.getElementById("totalDepartments").textContent =
            departments.size;

    } catch (error) {
        console.error(error);
        showMessage("registerMessage", "Unable to load students: " + error.message, false);
    }
}

// Display students in directory
function displayStudents(students) {
    const table = document.getElementById("studentTable");
    table.innerHTML = "";

    students.forEach(student => {
        const row = document.createElement("tr");

        [
            student.studentId,
            student.name,
            student.email,
            student.department,
            student.year,
            student.gpa
        ].forEach(value => {
            const cell = document.createElement("td");
            cell.textContent = value ?? "-";
            row.appendChild(cell);
        });

        table.appendChild(row);
    });
}

// Register student
document.getElementById("studentForm").addEventListener("submit", async function(event) {
    event.preventDefault();

    const student = {
        studentId: document.getElementById("studentId").value.trim(),
        name: document.getElementById("name").value.trim(),
        email: document.getElementById("email").value.trim(),
        department: document.getElementById("department").value.trim(),
        year: Number(document.getElementById("year").value),
        gpa: Number(document.getElementById("gpa").value)
    };

    try {
        await apiRequest(API, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(student)
        });

        showMessage("registerMessage", "Student registered successfully!");

        this.reset();
        loadStudents();

    } catch (error) {
        showMessage("registerMessage", "Registration failed: " + error.message, false);
    }
});

// Search students
document.getElementById("searchInput").addEventListener("input", async function() {
    try {
        const students = await apiRequest(API);

        const searchText = this.value.toLowerCase();

        const filteredStudents = students.filter(student =>
            student.name.toLowerCase().includes(searchText) ||
            student.studentId.toLowerCase().includes(searchText) ||
            student.department.toLowerCase().includes(searchText)
        );

        displayStudents(filteredStudents);

    } catch (error) {
        console.error(error);
    }
});

// Add student connection
document.getElementById("connectBtn").addEventListener("click", async function() {
    const student1 = document.getElementById("studentOne").value.trim();
    const student2 = document.getElementById("studentTwo").value.trim();

    if (!student1 || !student2) {
        showMessage("connectionMessage", "Enter both Student IDs.", false);
        return;
    }

    try {
        await apiRequest(
            `${API}/${encodeURIComponent(student1)}/connections/${encodeURIComponent(student2)}`,
            { method: "POST" }
        );

        showMessage("connectionMessage", "Connection added successfully!");

        document.getElementById("studentOne").value = "";
        document.getElementById("studentTwo").value = "";

        loadNetwork();

    } catch (error) {
        showMessage("connectionMessage", "Failed: " + error.message, false);
    }
});
// Remove student connection
document.getElementById("removeConnectionBtn").addEventListener("click", async function() {

    const student1 = document.getElementById("studentOne").value.trim();
    const student2 = document.getElementById("studentTwo").value.trim();

    if (!student1 || !student2) {
        showMessage("connectionMessage", "Enter both Student IDs.", false);
        return;
    }

    if (student1 === student2) {
        showMessage("connectionMessage", "Cannot disconnect a student from themselves.", false);
        return;
    }

    try {
        await apiRequest(
            `${API}/${encodeURIComponent(student1)}/connections/${encodeURIComponent(student2)}`,
            { method: "DELETE" }
        );

        showMessage("connectionMessage", "Connection removed successfully!");

        document.getElementById("studentOne").value = "";
        document.getElementById("studentTwo").value = "";

        loadNetwork();

    } catch (error) {
        showMessage("connectionMessage", "Failed: " + error.message, false);
    }
});

// View a student's connections
document.getElementById("viewConnectionsBtn").addEventListener("click", async function() {
    const studentId = document.getElementById("connectionStudentId").value.trim();

    if (!studentId) {
        showMessage("connectionsResult", "Enter a Student ID.", false);
        return;
    }

    try {
        const connections = await apiRequest(
            `${API}/${encodeURIComponent(studentId)}/connections`
        );

        document.getElementById("connectionsResult").textContent =
            connections.length
                ? `Connections of ${studentId}: ${connections.join(", ")}`
                : `No connections found for ${studentId}.`;

    } catch (error) {
        showMessage("connectionsResult", error.message, false);
    }
});

// Find shortest path using BFS
document.getElementById("findPathBtn").addEventListener("click", async function() {
    const from = document.getElementById("fromStudent").value.trim();
    const to = document.getElementById("toStudent").value.trim();

    if (!from || !to) {
        showMessage("pathResult", "Enter both Student IDs.", false);
        return;
    }

    try {
        const path = await apiRequest(
            `${API}/shortest-path?from=${encodeURIComponent(from)}&to=${encodeURIComponent(to)}`
        );

        document.getElementById("pathResult").textContent =
            path && path.length
                ? "Shortest path: " + path.join(" → ")
                : "No connection path found.";

    } catch (error) {
        showMessage("pathResult", error.message, false);
    }
});

// Load GPA leaderboard
document.getElementById("loadLeaderboardBtn").addEventListener("click", loadLeaderboard);

async function loadLeaderboard() {
    try {
        const students = await apiRequest(`${API}/leaderboard`);

        const table = document.getElementById("leaderboardTable");
        table.innerHTML = "";

        students.forEach((student, index) => {
            const row = document.createElement("tr");

            [index + 1, student.studentId, student.name, student.gpa]
                .forEach(value => {
                    const cell = document.createElement("td");
                    cell.textContent = value ?? "-";
                    row.appendChild(cell);
                });

            table.appendChild(row);
        });

    } catch (error) {
        showMessage("leaderboardTable", "Unable to load leaderboard: " + error.message, false);
    }
}

// Search student using BST
document.getElementById("bstSearchBtn").addEventListener("click", async function() {
    const studentId = document.getElementById("bstStudentId").value.trim();

    if (!studentId) {
        showMessage("bstResult", "Enter a Student ID.", false);
        return;
    }

    try {
        const result = await apiRequest(
            `${API}/bst/${encodeURIComponent(studentId)}`
        );

        document.getElementById("bstResult").textContent =
            result === true ? "Student found in BST!" :
            result === false ? "Student not found in BST." :
            JSON.stringify(result);

    } catch (error) {
        showMessage("bstResult", error.message, false);
    }
});

// BST Inorder Traversal
document.getElementById("traversalBtn").addEventListener("click", async function() {
    try {
        const result = await apiRequest(`${API}/bst/traversal/all`);

        document.getElementById("traversalResult").textContent =
            "Inorder Traversal: " + result.join(" → ");

    } catch (error) {
        showMessage("traversalResult", error.message, false);
    }
});

// Load campus network
document.getElementById("loadNetworkBtn").addEventListener("click", loadNetwork);

async function loadNetwork() {
    try {
        const network = await apiRequest(`${API}/network`);

        const result = document.getElementById("networkResult");
        result.innerHTML = "";

        let connectionCount = 0;

        Object.entries(network).forEach(([studentId, connections]) => {
            const paragraph = document.createElement("p");

            paragraph.textContent =
                `${studentId}  →  ${connections.join(", ") || "No connections"}`;

            result.appendChild(paragraph);

            connectionCount += connections.length;
        });

        document.getElementById("totalConnections").textContent =
            connectionCount / 2;

    } catch (error) {
        showMessage("networkResult", error.message, false);
    }
}

// Initial dashboard loading
document.addEventListener("DOMContentLoaded", function() {
    loadStudents();
    loadNetwork();
});
// View Student Profile
document.getElementById("viewProfileBtn").addEventListener("click", async function () {

    const studentId = document.getElementById("profileStudentId").value.trim();
    const result = document.getElementById("profileResult");

    if (!studentId) {
        result.textContent = "Please enter a Student ID.";
        return;
    }

    try {
        const student = await apiRequest(
            `${API}/${encodeURIComponent(studentId)}`
        );

        result.innerHTML = "";

        const profileCard = document.createElement("div");
        profileCard.className = "profile-card";

        const heading = document.createElement("h3");
        heading.textContent = student.name;
        profileCard.appendChild(heading);

        const details = [
            ["Student ID", student.studentId],
            ["Email", student.email],
            ["Department", student.department],
            ["Year", student.year],
            ["GPA", student.gpa]
        ];

        details.forEach(([label, value]) => {
            const p = document.createElement("p");
            p.textContent = `${label}: ${value ?? "N/A"}`;
            profileCard.appendChild(p);
        });

        result.appendChild(profileCard);

    } catch (error) {
        result.textContent = "Student not found!";
    }
});
// Update Student Details
document.getElementById("updateStudentBtn").addEventListener("click", async function () {

    const studentId = document.getElementById("editStudentId").value.trim();
    const name = document.getElementById("editName").value.trim();
    const email = document.getElementById("editEmail").value.trim();
    const department = document.getElementById("editDepartment").value.trim();
    const year = document.getElementById("editYear").value;
    const gpa = document.getElementById("editGpa").value;

    const message = document.getElementById("editStudentMessage");

    if (!studentId || !name || !email || !department || !year || !gpa) {
        message.textContent = "Please fill all fields.";
        return;
    }

    try {
        await apiRequest(
            `${API}/${encodeURIComponent(studentId)}`,
            {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    name: name,
                    email: email,
                    department: department,
                    year: Number(year),
                    gpa: Number(gpa)
                })
            }
        );

        message.textContent = "Student details updated successfully!";

        loadStudents();

    } catch (error) {
        message.textContent = "Update failed: " + error.message;
    }
});
// Delete Student
document.getElementById("deleteStudentBtn").addEventListener("click", async function () {

    const studentId = document.getElementById("deleteStudentId").value.trim();
    const message = document.getElementById("deleteStudentMessage");

    if (!studentId) {
        message.textContent = "Please enter Student ID.";
        return;
    }

    if (!confirm("Are you sure you want to delete this student?")) {
        return;
    }

    try {
        await apiRequest(
            `${API}/${encodeURIComponent(studentId)}`,
            { method: "DELETE" }
        );

        message.textContent = "Student deleted successfully!";

        document.getElementById("deleteStudentId").value = "";

        loadStudents();
        loadNetwork();

    } catch (error) {
        message.textContent = "Delete failed: " + error.message;
    }
});
// Hash Table Search
document.getElementById("hashSearchBtn").addEventListener("click", async function () {

    const studentId = document.getElementById("hashStudentId").value.trim();
    const result = document.getElementById("hashResult");

    if (!studentId) {
        result.textContent = "Please enter Student ID.";
        return;
    }

    try {
        const student = await apiRequest(
            `${API}/hash/${encodeURIComponent(studentId)}`
        );

        result.innerHTML = "";

        const card = document.createElement("div");
        card.className = "profile-card";

        const heading = document.createElement("h3");
        heading.textContent = "Student Found!";
        card.appendChild(heading);

        const details = [
            ["Student ID", student.studentId],
            ["Name", student.name],
            ["Email", student.email],
            ["Department", student.department],
            ["Year", student.year],
            ["GPA", student.gpa]
        ];

        details.forEach(([label, value]) => {
            const p = document.createElement("p");
            p.textContent = `${label}: ${value ?? "N/A"}`;
            card.appendChild(p);
        });

        result.appendChild(card);

    } catch (error) {
        result.textContent = "Student not found in Hash Table!";
    }
});