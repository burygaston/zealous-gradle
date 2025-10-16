// Dashboard JavaScript for Task Manager

let allTasks = [];
let allLabels = [];
let currentFilter = 'all';
let editingTaskId = null;

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    initializeEventListeners();
    loadLabels();
    loadTasks();
});

// Event Listeners
function initializeEventListeners() {
    // New task button
    document.getElementById('newTaskBtn').addEventListener('click', openNewTaskModal);
    document.getElementById('emptyNewTaskBtn')?.addEventListener('click', openNewTaskModal);

    // Modal controls
    document.getElementById('closeModal').addEventListener('click', closeModal);
    document.getElementById('cancelBtn').addEventListener('click', closeModal);
    document.querySelector('.modal-overlay').addEventListener('click', closeModal);

    // Task form submission
    document.getElementById('taskForm').addEventListener('submit', handleTaskSubmit);

    // Navigation filters
    document.querySelectorAll('.nav-link').forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const filter = this.getAttribute('data-filter');
            setFilter(filter);
        });
    });
}

// Load tasks from API
async function loadTasks() {
    try {
        const response = await fetch('/api/workitems');
        if (response.ok) {
            allTasks = await response.json();
            renderTasks();
            updateStatistics();
        } else {
            console.error('Failed to load tasks');
        }
    } catch (error) {
        console.error('Error loading tasks:', error);
    }
}

// Load labels from API
async function loadLabels() {
    try {
        const response = await fetch('/api/labels');
        if (response.ok) {
            allLabels = await response.json();
            renderLabelOptions();
        } else {
            console.error('Failed to load labels');
        }
    } catch (error) {
        console.error('Error loading labels:', error);
    }
}

// Render tasks based on current filter
function renderTasks() {
    const taskList = document.getElementById('taskList');
    const emptyState = document.getElementById('emptyState');

    let filteredTasks = filterTasks(allTasks, currentFilter);

    if (filteredTasks.length === 0) {
        taskList.style.display = 'none';
        emptyState.style.display = 'block';
        return;
    }

    taskList.style.display = 'block';
    emptyState.style.display = 'none';

    taskList.innerHTML = filteredTasks.map(task => createTaskCard(task)).join('');

    // Add event listeners to task cards
    document.querySelectorAll('.task-card').forEach(card => {
        const taskId = card.getAttribute('data-task-id');
        card.addEventListener('click', function(e) {
            if (!e.target.closest('.task-actions')) {
                openEditTaskModal(taskId);
            }
        });
    });

    // Add event listeners to delete buttons
    document.querySelectorAll('.btn-delete').forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.stopPropagation();
            const taskId = this.getAttribute('data-task-id');
            deleteTask(taskId);
        });
    });
}

// Filter tasks
function filterTasks(tasks, filter) {
    if (filter === 'all') {
        return tasks;
    } else if (filter === 'overdue') {
        return tasks.filter(task => task.overdue && task.status !== 'COMPLETE');
    } else {
        return tasks.filter(task => task.status === filter);
    }
}

// Create task card HTML
function createTaskCard(task) {
    const deadline = new Date(task.deadline);
    const isOverdue = task.overdue && task.status !== 'COMPLETE';
    const overdueClass = isOverdue ? 'overdue' : '';

    return `
        <div class="task-card status-${task.status} ${overdueClass}" data-task-id="${task.id}">
            <div class="task-header">
                <div>
                    <h3 class="task-title">${escapeHtml(task.title)}</h3>
                </div>
                <div class="task-actions">
                    <button class="btn-icon btn-delete" data-task-id="${task.id}" title="Delete">🗑️</button>
                </div>
            </div>
            ${task.description ? `<p class="task-description">${escapeHtml(task.description)}</p>` : ''}
            <div class="task-meta">
                <div class="meta-item">
                    <span class="icon">📅</span>
                    <span>${formatDate(deadline)}</span>
                </div>
                <div class="meta-item">
                    <span class="icon">⏰</span>
                    <span>${formatTime(deadline)}</span>
                </div>
                ${task.priority > 0 ? `
                <div class="meta-item">
                    <span class="icon">${getPriorityIcon(task.priority)}</span>
                    <span>${getPriorityLabel(task.priority)}</span>
                </div>
                ` : ''}
            </div>
            <div class="task-footer">
                <div class="task-labels">
                    ${task.labels && task.labels.length > 0 ?
                        task.labels.map(label => `
                            <span class="label-tag" style="background: ${label.color}22; color: ${label.color}; border-color: ${label.color}55;">
                                ${label.icon ? label.icon + ' ' : ''}${escapeHtml(label.name)}
                            </span>
                        `).join('') : ''
                    }
                </div>
                <span class="status-badge ${task.status}">${formatStatus(task.status)}</span>
            </div>
        </div>
    `;
}

// Render label options in modal
function renderLabelOptions() {
    const labelList = document.getElementById('labelList');
    if (allLabels.length === 0) {
        labelList.innerHTML = '<p style="color: var(--text-secondary); font-size: 0.9rem;">No labels available</p>';
        return;
    }

    labelList.innerHTML = allLabels.map(label => `
        <input type="checkbox" class="label-checkbox" id="label-${label.id}" value="${label.id}">
        <label class="label-option" for="label-${label.id}" style="border-color: ${label.color}55;">
            <span>${label.icon || ''}</span>
            <span>${escapeHtml(label.name)}</span>
        </label>
    `).join('');
}

// Open new task modal
function openNewTaskModal() {
    editingTaskId = null;
    document.getElementById('modalTitle').textContent = 'New Task';
    document.getElementById('taskForm').reset();
    document.getElementById('taskId').value = '';

    // Set default deadline to tomorrow
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    tomorrow.setHours(17, 0, 0, 0);
    document.getElementById('taskDeadline').value = formatDateTimeLocal(tomorrow);

    // Uncheck all labels
    document.querySelectorAll('.label-checkbox').forEach(cb => cb.checked = false);

    document.getElementById('taskModal').style.display = 'block';
}

// Open edit task modal
function openEditTaskModal(taskId) {
    const task = allTasks.find(t => t.id == taskId);
    if (!task) return;

    editingTaskId = taskId;
    document.getElementById('modalTitle').textContent = 'Edit Task';
    document.getElementById('taskId').value = task.id;
    document.getElementById('taskTitle').value = task.title;
    document.getElementById('taskDescription').value = task.description || '';
    document.getElementById('taskStatus').value = task.status;
    document.getElementById('taskPriority').value = task.priority;
    document.getElementById('taskDeadline').value = formatDateTimeLocal(new Date(task.deadline));

    // Check labels
    document.querySelectorAll('.label-checkbox').forEach(cb => cb.checked = false);
    if (task.labels) {
        task.labels.forEach(label => {
            const checkbox = document.getElementById(`label-${label.id}`);
            if (checkbox) checkbox.checked = true;
        });
    }

    document.getElementById('taskModal').style.display = 'block';
}

// Close modal
function closeModal() {
    document.getElementById('taskModal').style.display = 'none';
    editingTaskId = null;
}

// Handle task form submission
async function handleTaskSubmit(e) {
    e.preventDefault();

    const taskData = {
        title: document.getElementById('taskTitle').value,
        description: document.getElementById('taskDescription').value,
        status: document.getElementById('taskStatus').value,
        priority: parseInt(document.getElementById('taskPriority').value),
        deadline: new Date(document.getElementById('taskDeadline').value).toISOString(),
        labels: getSelectedLabels()
    };

    try {
        let response;
        if (editingTaskId) {
            // Update existing task
            response = await fetch(`/api/workitems/${editingTaskId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(taskData)
            });
        } else {
            // Create new task
            response = await fetch('/api/workitems', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(taskData)
            });
        }

        if (response.ok) {
            closeModal();
            await loadTasks();
        } else {
            alert('Failed to save task');
        }
    } catch (error) {
        console.error('Error saving task:', error);
        alert('Error saving task');
    }
}

// Delete task
async function deleteTask(taskId) {
    if (!confirm('Are you sure you want to delete this task?')) {
        return;
    }

    try {
        const response = await fetch(`/api/workitems/${taskId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            await loadTasks();
        } else {
            alert('Failed to delete task');
        }
    } catch (error) {
        console.error('Error deleting task:', error);
        alert('Error deleting task');
    }
}

// Get selected labels
function getSelectedLabels() {
    const selected = [];
    document.querySelectorAll('.label-checkbox:checked').forEach(checkbox => {
        const label = allLabels.find(l => l.id == checkbox.value);
        if (label) selected.push(label);
    });
    return selected;
}

// Set filter
function setFilter(filter) {
    currentFilter = filter;

    // Update active nav link
    document.querySelectorAll('.nav-link').forEach(link => {
        link.classList.remove('active');
        if (link.getAttribute('data-filter') === filter) {
            link.classList.add('active');
        }
    });

    // Update title
    const titles = {
        'all': 'All Tasks',
        'TODO': 'To Do',
        'IN_PROGRESS': 'In Progress',
        'COMPLETE': 'Complete',
        'overdue': 'Overdue Tasks'
    };
    document.getElementById('filterTitle').textContent = titles[filter] || 'Tasks';

    renderTasks();
}

// Update statistics
function updateStatistics() {
    const total = allTasks.length;
    const todo = allTasks.filter(t => t.status === 'TODO').length;
    const inProgress = allTasks.filter(t => t.status === 'IN_PROGRESS').length;
    const complete = allTasks.filter(t => t.status === 'COMPLETE').length;
    const overdue = allTasks.filter(t => t.overdue && t.status !== 'COMPLETE').length;

    document.getElementById('statTotal').textContent = total;
    document.getElementById('statTodo').textContent = todo;
    document.getElementById('statInProgress').textContent = inProgress;
    document.getElementById('statComplete').textContent = complete;
    document.getElementById('statOverdue').textContent = overdue;
}

// Utility functions
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function formatDate(date) {
    const options = { year: 'numeric', month: 'short', day: 'numeric' };
    return date.toLocaleDateString('en-US', options);
}

function formatTime(date) {
    const options = { hour: '2-digit', minute: '2-digit' };
    return date.toLocaleTimeString('en-US', options);
}

function formatDateTimeLocal(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day}T${hours}:${minutes}`;
}

function formatStatus(status) {
    const statuses = {
        'TODO': 'To Do',
        'IN_PROGRESS': 'In Progress',
        'COMPLETE': 'Complete'
    };
    return statuses[status] || status;
}

function getPriorityLabel(priority) {
    const labels = ['Low', 'Medium', 'High', 'Critical'];
    return labels[priority] || 'Low';
}

function getPriorityIcon(priority) {
    const icons = ['⬇️', '➡️', '⬆️', '🔴'];
    return icons[priority] || '⬇️';
}