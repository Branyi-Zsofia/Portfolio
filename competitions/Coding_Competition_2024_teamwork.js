"use strict";

const fs = require("fs");

process.stdin.resume();
process.stdin.setEncoding("utf-8");

let inputString = "";
let currentLine = 0;

process.stdin.on("data", function (inputStdin) {
  inputString += inputStdin;
});

process.stdin.on("end", function () {
  inputString = inputString.split("\n");

  main();
});

function readLine() {
  return inputString[currentLine++];
}

/*
 * Complete the 'optimize' function below.
 *
 * The function is expected to return a 2D_INTEGER_ARRAY.
 * The function accepts following parameters:
 *  1. 2D_INTEGER_ARRAY server_list
 *  2. 2D_INTEGER_ARRAY task_list
 */

/*
INPUT
5
100 50
150 80
200 150
150 70
180 100
7
1 30 3 100
2 20 2 150
3 50 1 200
4 50 1 50
5 80 4 150
6 20 1 30
7 10 2 50
*/

/*
OUTPUT
4
3 7
6 2
1
5
260
*/

/*
servers_list = [[100,50], [150,80], [200,150], [150,70], [180,100]

tasks_list = [[1, 30, 3, 100], [2, 20, 2, 150], [3, 50, 1, 200], [4, 50, 1, 50], [5, 80, 4, 150], [6, 20, 1, 30], [7, 10, 2, 50]
Function should return list of tuples with task_id distributed to servers. last element would be total enerygy conumption.

result = [[4], [3,7], [6,2], [1], [5], [260]]*/
/*
**Task Sorting**
- Sort tasks by priority, then by deadline.
*/

/*
**Task Sorting**
- Sort tasks by priority, then by deadline.

**Server Distribution**
- For each task in the sorted list:
  - Find the server with the most capacity left (most room to add tasks)
  - Check if the server can accommodate the task (if load + task duration ≤ server capacity)
  - If so, add the task to that server's task list and update the server's load.
  - If not, continue to the next server.

**Energy Consumption Calculation**
- Initialize total energy consumption to 0
- For each server:
  - Calculate energy consumption by summing the product of each task's duration and the server's load
  - Add this to the total energy consumption

**Output**
- Output the list of tasks for each server
- Output the total energy consumption
*/

// Function to assign tasks to servers
function optimize(servers, tasks) {
  const serverAssignments = {}; // Dictionary to hold server assignments

  // Initialize server assignments with empty arrays for each server
  for (let i = 0; i < servers.length; i++) {
    serverAssignments[i] = [];
  }

  // Step 1: Sort tasks by priority, then by deadline
  tasks.sort((a, b) => {
    if (a[2] !== b[2]) {
      return a[2] - b[2]; // By priority
    }
    return a[3] - b[3]; // By deadline
  });

  let totalEnergy = 0; // Total energy consumption
  let flipa = true;
  // Step 2: Assign tasks to servers based on capacity and load
  //---------------------------
  for (const task of tasks) {
    const [taskId, taskSize] = task; // Task details

    if (!flipa) {
      console.log("we be balling here " + flipa);
      // Find a suitable server
      for (let i = 0; i < servers.length; i++) {
        const [capacity, currentLoad] = servers[i]; // Server capacity and load

        // Check if the task can fit into this server
        if (currentLoad + taskSize <= capacity) {
          serverAssignments[i].push(taskId); // Assign task
          servers[i][1] += taskSize; // Update the server's load

          // Calculate energy consumption for this task
          totalEnergy += taskSize;
          flipa = true;
          console.log(taskId);
          break; // No need to continue
        }
      }
    } else {
      for (let i = servers.length - 1; i >= 0; i--) {
        const [capacity, currentLoad] = servers[i]; // Server capacity and load

        // Check if the task can fit into this server

        if (currentLoad + taskSize <= capacity) {
          serverAssignments[i].push(taskId); // Assign task
          servers[i][1] += taskSize; // Update the server's load

          // Calculate energy consumption for this task
          totalEnergy += taskSize;
          flipa = false;
          console.log(taskId);
          break; // No need to continue
        }
      }
    }
  }

  // Step 3: Create output structure
  const result = [];
  for (const key in serverAssignments) {
    result.push(serverAssignments[key]); // Add tasks for each server
  }
  result.push([totalEnergy]); // Add the total energy consumption

  return result; // Return the result
}

function main() {
  const ws = fs.createWriteStream("/home/skver/bunozes/output.txt");
  const num_servers = parseInt(readLine().trim(), 10);

  let servers = Array(num_servers);

  for (let i = 0; i < num_servers; i++) {
    servers[i] = readLine()
      .replace(/\s+$/g, "")
      .split(" ")
      .map((serversTemp) => parseInt(serversTemp, 10));
  }

  const num_tasks = parseInt(readLine().trim(), 10);

  let tasks = Array(num_tasks);

  for (let i = 0; i < num_tasks; i++) {
    tasks[i] = readLine()
      .replace(/\s+$/g, "")
      .split(" ")
      .map((tasksTemp) => parseInt(tasksTemp, 10));
  }

  const result = optimize(servers, tasks);
  console.log(result);
  ws.write(result.map((x) => x.join(" ")).join("\n") + "\n");

  ws.end();
}
