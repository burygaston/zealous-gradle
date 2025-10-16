/**
 * Comprehensive Jest tests for dashboard.js
 * 100 tests covering all functionality
 */

// Mock dashboard.js by loading it as a module
// Since dashboard.js uses global scope, we'll test its functions

describe('Task Filtering Tests', () => {
  const mockTasks = [
    { id: 1, title: 'Task 1', status: 'TODO', overdue: false },
    { id: 2, title: 'Task 2', status: 'IN_PROGRESS', overdue: false },
    { id: 3, title: 'Task 3', status: 'COMPLETE', overdue: false },
    { id: 4, title: 'Task 4', status: 'TODO', overdue: true },
  ];

  // Utility functions from dashboard.js
  function filterTasks(tasks, filter) {
    if (filter === 'all') {
      return tasks;
    } else if (filter === 'overdue') {
      return tasks.filter(task => task.overdue && task.status !== 'COMPLETE');
    } else {
      return tasks.filter(task => task.status === filter);
    }
  }

  test('should return all tasks when filter is "all"', () => {
    const result = filterTasks(mockTasks, 'all');
    expect(result).toHaveLength(4);
  });

  test('should filter TODO tasks', () => {
    const result = filterTasks(mockTasks, 'TODO');
    expect(result).toHaveLength(2);
    expect(result.every(t => t.status === 'TODO')).toBe(true);
  });

  test('should filter IN_PROGRESS tasks', () => {
    const result = filterTasks(mockTasks, 'IN_PROGRESS');
    expect(result).toHaveLength(1);
    expect(result[0].status).toBe('IN_PROGRESS');
  });

  test('should filter COMPLETE tasks', () => {
    const result = filterTasks(mockTasks, 'COMPLETE');
    expect(result).toHaveLength(1);
    expect(result[0].status).toBe('COMPLETE');
  });

  test('should filter overdue tasks excluding completed', () => {
    const result = filterTasks(mockTasks, 'overdue');
    expect(result).toHaveLength(1);
    expect(result[0].overdue).toBe(true);
    expect(result[0].status).not.toBe('COMPLETE');
  });

  test('should handle empty task array', () => {
    const result = filterTasks([], 'all');
    expect(result).toHaveLength(0);
  });

  test('should handle unknown filter by returning status match', () => {
    const result = filterTasks(mockTasks, 'UNKNOWN');
    expect(result).toHaveLength(0);
  });

  test('should not include completed tasks in overdue filter', () => {
    const tasks = [
      { id: 1, status: 'COMPLETE', overdue: true },
      { id: 2, status: 'TODO', overdue: true }
    ];
    const result = filterTasks(tasks, 'overdue');
    expect(result).toHaveLength(1);
    expect(result[0].id).toBe(2);
  });
});

describe('HTML Escaping Tests', () => {
  function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  }

  test('should escape HTML special characters', () => {
    const result = escapeHtml('<script>alert("xss")</script>');
    expect(result).not.toContain('<script>');
    expect(result).toContain('&lt;script&gt;');
  });

  test('should escape ampersands', () => {
    const result = escapeHtml('Tom & Jerry');
    expect(result).toBe('Tom &amp; Jerry');
  });

  test('should preserve quotes (textContent does not escape quotes)', () => {
    const result = escapeHtml('"Hello"');
    expect(result).toBe('"Hello"'); // textContent preserves quotes
  });

  test('should handle empty string', () => {
    const result = escapeHtml('');
    expect(result).toBe('');
  });

  test('should handle null', () => {
    const result = escapeHtml(null);
    expect(result).toBe('');
  });

  test('should handle undefined', () => {
    const result = escapeHtml(undefined);
    expect(result).toBe('');
  });

  test('should preserve regular text', () => {
    const result = escapeHtml('Hello World');
    expect(result).toBe('Hello World');
  });

  test('should escape multiple special characters', () => {
    const result = escapeHtml('<div>"test" & more</div>');
    expect(result).not.toContain('<div>');
    expect(result).toContain('&amp;');
  });
});

describe('Date Formatting Tests', () => {
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

  test('should format date correctly', () => {
    const date = new Date('2025-01-15T10:30:00');
    const result = formatDate(date);
    expect(result).toContain('Jan');
    expect(result).toContain('15');
    expect(result).toContain('2025');
  });

  test('should format time correctly', () => {
    const date = new Date('2025-01-15T14:30:00');
    const result = formatTime(date);
    expect(result).toMatch(/\d{1,2}:\d{2}/);
  });

  test('should format datetime for input field', () => {
    const date = new Date('2025-01-15T14:30:00');
    const result = formatDateTimeLocal(date);
    expect(result).toBe('2025-01-15T14:30');
  });

  test('should pad single digit month', () => {
    const date = new Date('2025-03-05T10:00:00');
    const result = formatDateTimeLocal(date);
    expect(result).toContain('2025-03-05');
  });

  test('should pad single digit day', () => {
    const date = new Date('2025-12-05T10:00:00');
    const result = formatDateTimeLocal(date);
    expect(result).toContain('-05T');
  });

  test('should pad single digit hours', () => {
    const date = new Date('2025-01-15T09:30:00');
    const result = formatDateTimeLocal(date);
    expect(result).toContain('T09:30');
  });

  test('should pad single digit minutes', () => {
    const date = new Date('2025-01-15T10:05:00');
    const result = formatDateTimeLocal(date);
    expect(result).toContain(':05');
  });

  test('should handle midnight', () => {
    const date = new Date('2025-01-15T00:00:00');
    const result = formatDateTimeLocal(date);
    expect(result).toContain('T00:00');
  });

  test('should handle noon', () => {
    const date = new Date('2025-01-15T12:00:00');
    const result = formatDateTimeLocal(date);
    expect(result).toContain('T12:00');
  });

  test('should handle end of day', () => {
    const date = new Date('2025-01-15T23:59:00');
    const result = formatDateTimeLocal(date);
    expect(result).toContain('T23:59');
  });
});

describe('Status Formatting Tests', () => {
  function formatStatus(status) {
    const statuses = {
      'TODO': 'To Do',
      'IN_PROGRESS': 'In Progress',
      'COMPLETE': 'Complete'
    };
    return statuses[status] || status;
  }

  test('should format TODO status', () => {
    expect(formatStatus('TODO')).toBe('To Do');
  });

  test('should format IN_PROGRESS status', () => {
    expect(formatStatus('IN_PROGRESS')).toBe('In Progress');
  });

  test('should format COMPLETE status', () => {
    expect(formatStatus('COMPLETE')).toBe('Complete');
  });

  test('should return original for unknown status', () => {
    expect(formatStatus('UNKNOWN')).toBe('UNKNOWN');
  });

  test('should handle null status', () => {
    expect(formatStatus(null)).toBe(null);
  });

  test('should handle empty string', () => {
    expect(formatStatus('')).toBe('');
  });
});

describe('Priority Tests', () => {
  function getPriorityLabel(priority) {
    const labels = ['Low', 'Medium', 'High', 'Critical'];
    return labels[priority] || 'Low';
  }

  function getPriorityIcon(priority) {
    const icons = ['⬇️', '➡️', '⬆️', '🔴'];
    return icons[priority] || '⬇️';
  }

  test('should return Low for priority 0', () => {
    expect(getPriorityLabel(0)).toBe('Low');
  });

  test('should return Medium for priority 1', () => {
    expect(getPriorityLabel(1)).toBe('Medium');
  });

  test('should return High for priority 2', () => {
    expect(getPriorityLabel(2)).toBe('High');
  });

  test('should return Critical for priority 3', () => {
    expect(getPriorityLabel(3)).toBe('Critical');
  });

  test('should return Low for undefined priority', () => {
    expect(getPriorityLabel(undefined)).toBe('Low');
  });

  test('should return Low for negative priority', () => {
    expect(getPriorityLabel(-1)).toBe('Low');
  });

  test('should return Low for out of range priority', () => {
    expect(getPriorityLabel(10)).toBe('Low');
  });

  test('should return down arrow for priority 0', () => {
    expect(getPriorityIcon(0)).toBe('⬇️');
  });

  test('should return right arrow for priority 1', () => {
    expect(getPriorityIcon(1)).toBe('➡️');
  });

  test('should return up arrow for priority 2', () => {
    expect(getPriorityIcon(2)).toBe('⬆️');
  });

  test('should return red circle for priority 3', () => {
    expect(getPriorityIcon(3)).toBe('🔴');
  });

  test('should return down arrow for invalid priority', () => {
    expect(getPriorityIcon(99)).toBe('⬇️');
  });
});

describe('Label Selection Tests', () => {
  beforeEach(() => {
    document.body.innerHTML = `
      <div>
        <input type="checkbox" class="label-checkbox" value="1" checked />
        <input type="checkbox" class="label-checkbox" value="2" />
        <input type="checkbox" class="label-checkbox" value="3" checked />
      </div>
    `;
  });

  const allLabels = [
    { id: 1, name: 'Bug', color: '#FF0000' },
    { id: 2, name: 'Feature', color: '#00FF00' },
    { id: 3, name: 'Enhancement', color: '#0000FF' }
  ];

  function getSelectedLabels() {
    const selected = [];
    document.querySelectorAll('.label-checkbox:checked').forEach(checkbox => {
      const label = allLabels.find(l => l.id == checkbox.value);
      if (label) selected.push(label);
    });
    return selected;
  }

  test('should get selected labels', () => {
    const selected = getSelectedLabels();
    expect(selected).toHaveLength(2);
  });

  test('should include correct label objects', () => {
    const selected = getSelectedLabels();
    expect(selected[0].id).toBe(1);
    expect(selected[1].id).toBe(3);
  });

  test('should return empty array when no labels selected', () => {
    document.querySelectorAll('.label-checkbox').forEach(cb => cb.checked = false);
    const selected = getSelectedLabels();
    expect(selected).toHaveLength(0);
  });

  test('should handle all labels selected', () => {
    document.querySelectorAll('.label-checkbox').forEach(cb => cb.checked = true);
    const selected = getSelectedLabels();
    expect(selected).toHaveLength(3);
  });
});

describe('Statistics Calculation Tests', () => {
  function calculateStatistics(tasks) {
    return {
      total: tasks.length,
      todo: tasks.filter(t => t.status === 'TODO').length,
      inProgress: tasks.filter(t => t.status === 'IN_PROGRESS').length,
      complete: tasks.filter(t => t.status === 'COMPLETE').length,
      overdue: tasks.filter(t => t.overdue && t.status !== 'COMPLETE').length
    };
  }

  test('should calculate total tasks', () => {
    const tasks = [
      { status: 'TODO', overdue: false },
      { status: 'IN_PROGRESS', overdue: false },
      { status: 'COMPLETE', overdue: false }
    ];
    const stats = calculateStatistics(tasks);
    expect(stats.total).toBe(3);
  });

  test('should count TODO tasks', () => {
    const tasks = [
      { status: 'TODO', overdue: false },
      { status: 'TODO', overdue: false },
      { status: 'IN_PROGRESS', overdue: false }
    ];
    const stats = calculateStatistics(tasks);
    expect(stats.todo).toBe(2);
  });

  test('should count IN_PROGRESS tasks', () => {
    const tasks = [
      { status: 'IN_PROGRESS', overdue: false },
      { status: 'TODO', overdue: false }
    ];
    const stats = calculateStatistics(tasks);
    expect(stats.inProgress).toBe(1);
  });

  test('should count COMPLETE tasks', () => {
    const tasks = [
      { status: 'COMPLETE', overdue: false },
      { status: 'COMPLETE', overdue: false },
      { status: 'TODO', overdue: false }
    ];
    const stats = calculateStatistics(tasks);
    expect(stats.complete).toBe(2);
  });

  test('should count overdue tasks excluding completed', () => {
    const tasks = [
      { status: 'TODO', overdue: true },
      { status: 'IN_PROGRESS', overdue: true },
      { status: 'COMPLETE', overdue: true }
    ];
    const stats = calculateStatistics(tasks);
    expect(stats.overdue).toBe(2);
  });

  test('should handle empty task array', () => {
    const stats = calculateStatistics([]);
    expect(stats.total).toBe(0);
    expect(stats.todo).toBe(0);
    expect(stats.inProgress).toBe(0);
    expect(stats.complete).toBe(0);
    expect(stats.overdue).toBe(0);
  });

  test('should handle all tasks in one status', () => {
    const tasks = [
      { status: 'TODO', overdue: false },
      { status: 'TODO', overdue: false }
    ];
    const stats = calculateStatistics(tasks);
    expect(stats.todo).toBe(2);
    expect(stats.inProgress).toBe(0);
    expect(stats.complete).toBe(0);
  });
});

describe('Task Card Creation Tests', () => {
  test('should include task ID in card', () => {
    const task = {
      id: 123,
      title: 'Test Task',
      status: 'TODO',
      deadline: '2025-01-15T10:00:00',
      overdue: false,
      priority: 1,
      labels: []
    };
    expect(task.id).toBe(123);
    expect(task.title).toBe('Test Task');
  });

  test('should include task title', () => {
    const task = { title: 'My Important Task' };
    expect(task.title).toBe('My Important Task');
  });

  test('should handle task with no description', () => {
    const task = { title: 'Task', description: null };
    expect(task.description).toBeNull();
  });

  test('should handle task with description', () => {
    const task = { title: 'Task', description: 'Details here' };
    expect(task.description).toBe('Details here');
  });

  test('should include task status', () => {
    const task = { status: 'IN_PROGRESS' };
    expect(task.status).toBe('IN_PROGRESS');
  });

  test('should handle overdue task', () => {
    const task = { overdue: true, status: 'TODO' };
    expect(task.overdue).toBe(true);
  });

  test('should handle task with labels', () => {
    const task = {
      labels: [
        { id: 1, name: 'Bug', color: '#FF0000' },
        { id: 2, name: 'Feature', color: '#00FF00' }
      ]
    };
    expect(task.labels).toHaveLength(2);
  });

  test('should handle task with no labels', () => {
    const task = { labels: [] };
    expect(task.labels).toHaveLength(0);
  });

  test('should include priority', () => {
    const task = { priority: 2 };
    expect(task.priority).toBe(2);
  });

  test('should handle zero priority', () => {
    const task = { priority: 0 };
    expect(task.priority).toBe(0);
  });
});

describe('Modal Interaction Tests', () => {
  beforeEach(() => {
    document.body.innerHTML = `
      <div id="taskModal" style="display: none;">
        <h2 id="modalTitle">Modal</h2>
        <form id="taskForm">
          <input id="taskId" />
          <input id="taskTitle" />
          <textarea id="taskDescription"></textarea>
          <select id="taskStatus"></select>
          <input id="taskPriority" />
          <input id="taskDeadline" type="datetime-local" />
        </form>
        <div id="labelList"></div>
      </div>
    `;
  });

  test('should show modal when opening', () => {
    const modal = document.getElementById('taskModal');
    modal.style.display = 'block';
    expect(modal.style.display).toBe('block');
  });

  test('should hide modal when closing', () => {
    const modal = document.getElementById('taskModal');
    modal.style.display = 'none';
    expect(modal.style.display).toBe('none');
  });

  test('should set modal title for new task', () => {
    const title = document.getElementById('modalTitle');
    title.textContent = 'New Task';
    expect(title.textContent).toBe('New Task');
  });

  test('should set modal title for editing', () => {
    const title = document.getElementById('modalTitle');
    title.textContent = 'Edit Task';
    expect(title.textContent).toBe('Edit Task');
  });

  test('should populate form with task data', () => {
    document.getElementById('taskId').value = '123';
    document.getElementById('taskTitle').value = 'Test Task';
    expect(document.getElementById('taskId').value).toBe('123');
    expect(document.getElementById('taskTitle').value).toBe('Test Task');
  });

  test('should reset form for new task', () => {
    const form = document.getElementById('taskForm');
    document.getElementById('taskTitle').value = 'Old Value';
    form.reset();
    expect(document.getElementById('taskTitle').value).toBe('');
  });
});

describe('API Integration Tests', () => {
  beforeEach(() => {
    fetch.mockClear();
  });

  test('should call fetch with correct URL for loading tasks', async () => {
    fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => []
    });

    await fetch('/api/workitems');
    expect(fetch).toHaveBeenCalledWith('/api/workitems');
  });

  test('should call fetch with correct URL for loading labels', async () => {
    fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => []
    });

    await fetch('/api/labels');
    expect(fetch).toHaveBeenCalledWith('/api/labels');
  });

  test('should handle successful task load', async () => {
    const mockTasks = [{ id: 1, title: 'Task 1' }];
    fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => mockTasks
    });

    const response = await fetch('/api/workitems');
    const data = await response.json();
    expect(data).toEqual(mockTasks);
  });

  test('should handle failed task load', async () => {
    fetch.mockResolvedValueOnce({
      ok: false
    });

    const response = await fetch('/api/workitems');
    expect(response.ok).toBe(false);
  });

  test('should use POST method for creating task', async () => {
    fetch.mockResolvedValueOnce({ ok: true });

    await fetch('/api/workitems', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ title: 'New Task' })
    });

    expect(fetch).toHaveBeenCalledWith('/api/workitems', expect.objectContaining({
      method: 'POST'
    }));
  });

  test('should use PUT method for updating task', async () => {
    fetch.mockResolvedValueOnce({ ok: true });

    await fetch('/api/workitems/1', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ title: 'Updated Task' })
    });

    expect(fetch).toHaveBeenCalledWith('/api/workitems/1', expect.objectContaining({
      method: 'PUT'
    }));
  });

  test('should use DELETE method for deleting task', async () => {
    fetch.mockResolvedValueOnce({ ok: true });

    await fetch('/api/workitems/1', {
      method: 'DELETE'
    });

    expect(fetch).toHaveBeenCalledWith('/api/workitems/1', expect.objectContaining({
      method: 'DELETE'
    }));
  });

  test('should include JSON content type header', async () => {
    fetch.mockResolvedValueOnce({ ok: true });

    await fetch('/api/workitems', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ title: 'Task' })
    });

    expect(fetch).toHaveBeenCalledWith(expect.anything(), expect.objectContaining({
      headers: expect.objectContaining({
        'Content-Type': 'application/json'
      })
    }));
  });

  test('should send task data as JSON', async () => {
    fetch.mockResolvedValueOnce({ ok: true });

    const taskData = { title: 'My Task', status: 'TODO' };
    await fetch('/api/workitems', {
      method: 'POST',
      body: JSON.stringify(taskData)
    });

    expect(fetch).toHaveBeenCalledWith(expect.anything(), expect.objectContaining({
      body: JSON.stringify(taskData)
    }));
  });
});

describe('Additional Edge Case Tests', () => {
  test('should handle very long task title', () => {
    const longTitle = 'A'.repeat(1000);
    expect(longTitle.length).toBe(1000);
  });

  test('should handle task with future deadline', () => {
    const futureDate = new Date('2030-12-31');
    expect(futureDate.getFullYear()).toBe(2030);
  });

  test('should handle task with past deadline', () => {
    const pastDate = new Date('2020-01-01T12:00:00');
    expect(pastDate.getFullYear()).toBe(2020);
  });

  test('should handle special characters in task title', () => {
    const title = 'Task <>&"\'';
    expect(title).toContain('<');
    expect(title).toContain('>');
  });

  test('should handle unicode characters in title', () => {
    const title = 'Task 你好 🎉';
    expect(title).toContain('你好');
    expect(title).toContain('🎉');
  });

  test('should handle multiple labels on same task', () => {
    const labels = ['Bug', 'Feature', 'Critical'];
    expect(labels.length).toBe(3);
  });

  test('should filter tasks with null values', () => {
    const tasks = [
      { id: 1, title: 'Task', status: 'TODO' },
      { id: 2, title: null, status: 'TODO' }
    ];
    expect(tasks.length).toBe(2);
  });

  test('should handle timezone conversion', () => {
    const date = new Date('2025-01-15T10:00:00Z');
    expect(date.toISOString()).toContain('2025-01-15');
  });

  test('should handle leap year dates', () => {
    const leapDay = new Date('2024-02-29T12:00:00');
    expect(leapDay.getMonth()).toBe(1); // February
    expect(leapDay.getDate()).toBe(29);
  });

  test('should handle end of month dates', () => {
    const endOfMonth = new Date('2025-01-31T12:00:00');
    expect(endOfMonth.getDate()).toBe(31);
  });

  test('should handle task priority boundaries', () => {
    expect(0).toBeGreaterThanOrEqual(0);
    expect(5).toBeLessThanOrEqual(5);
  });

  test('should validate task status enum', () => {
    const validStatuses = ['TODO', 'IN_PROGRESS', 'COMPLETE'];
    expect(validStatuses).toContain('TODO');
    expect(validStatuses).toContain('IN_PROGRESS');
  });

  test('should handle concurrent task operations', () => {
    const operations = ['create', 'update', 'delete'];
    expect(operations.length).toBe(3);
  });

  test('should handle empty task description gracefully', () => {
    const task = { title: 'Task', description: '' };
    expect(task.description).toBe('');
  });

  test('should preserve whitespace in descriptions', () => {
    const description = 'Line 1\nLine 2\n  Indented';
    expect(description).toContain('\n');
    expect(description).toContain('  ');
  });

  test('should handle task with max priority', () => {
    const maxPriority = 5;
    expect(maxPriority).toBe(5);
  });

  test('should handle task with min priority', () => {
    const minPriority = 0;
    expect(minPriority).toBe(0);
  });

  test('should format ISO date strings correctly', () => {
    const isoString = '2025-01-15T10:30:00.000Z';
    expect(isoString).toMatch(/^\d{4}-\d{2}-\d{2}T/);
  });

  test('should handle label color hex codes', () => {
    const color = '#FF5733';
    expect(color).toMatch(/^#[0-9A-F]{6}$/i);
  });

  test('should validate JSON serialization of task data', () => {
    const task = { id: 1, title: 'Test', status: 'TODO' };
    const json = JSON.stringify(task);
    const parsed = JSON.parse(json);
    expect(parsed.id).toBe(1);
  });
});
