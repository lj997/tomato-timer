<template>
  <div class="card">
    <h3 style="margin-bottom: 20px; font-size: 20px;">📋 今日待办</h3>
    
    <div class="add-task">
      <input 
        type="text" 
        v-model="newTaskTitle"
        placeholder="输入任务标题，按回车添加..."
        @keyup.enter="addTask"
      />
      <input 
        type="number" 
        v-model.number="newTaskPomodoros"
        min="1"
        max="20"
        style="width: 80px;"
      />
      <button class="btn btn-primary" @click="addTask">
        添加任务
      </button>
    </div>

    <div v-if="tasks.length === 0" class="empty-state">
      <div class="empty-state-icon">📝</div>
      <div class="empty-state-text">暂无任务，添加一个开始吧！</div>
    </div>

    <div v-else>
      <transition-group name="slide" tag="div">
        <div 
          v-for="task in tasks" 
          :key="task.id"
          :class="['task-item', { completed: task.status === 'completed' }]"
          :style="{ cursor: 'pointer' }"
          @click="selectTask(task)"
        >
          <div 
            :class="['task-checkbox', { checked: task.status === 'completed' }]"
            @click.stop="toggleTaskStatus(task)"
          >
            <span v-if="task.status === 'completed'">✓</span>
          </div>
          
          <template v-if="editingTaskId !== task.id">
            <div class="task-title">{{ task.title }}</div>
            <div class="task-pomodoro">
              <span v-for="i in task.estimatedPomodoros" :key="i">
                <span 
                  :class="['pomodoro-dot', { empty: i > task.completedPomodoros }]"
                ></span>
              </span>
              <span style="margin-left: 8px;">
                {{ task.completedPomodoros }}/{{ task.estimatedPomodoros }}
              </span>
            </div>
            <div class="task-actions">
              <button class="btn btn-sm btn-ghost" @click.stop="startEditTask(task)">
                编辑
              </button>
              <button class="btn btn-sm btn-ghost" @click.stop="deleteTask(task.id)">
                删除
              </button>
            </div>
          </template>
          
          <template v-else>
            <div class="task-edit-form">
              <input 
                type="text" 
                v-model="editTaskTitle"
                placeholder="任务标题"
                @keyup.enter="saveEditTask"
              />
              <input 
                type="number" 
                v-model.number="editTaskPomodoros"
                min="1"
                max="20"
                style="width: 80px;"
              />
            </div>
            <button class="btn btn-sm btn-primary" @click="saveEditTask">
              保存
            </button>
            <button class="btn btn-sm btn-ghost" @click="cancelEdit">
              取消
            </button>
          </template>
        </div>
      </transition-group>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { taskApi } from '../api/index.js'

const props = defineProps({
  selectedTaskId: {
    type: Number,
    default: null
  }
})

const emit = defineEmits(['select-task'])

const tasks = ref([])
const newTaskTitle = ref('')
const newTaskPomodoros = ref(1)
const editingTaskId = ref(null)
const editTaskTitle = ref('')
const editTaskPomodoros = ref(1)

const loadTasks = async () => {
  try {
    const response = await taskApi.getToday()
    tasks.value = response.data
  } catch (error) {
    console.error('加载任务失败:', error)
    tasks.value = []
  }
}

const addTask = async () => {
  if (!newTaskTitle.value.trim()) return
  
  try {
    const task = {
      title: newTaskTitle.value.trim(),
      estimatedPomodoros: newTaskPomodoros.value
    }
    
    const response = await taskApi.create(task)
    tasks.value.unshift(response.data)
    newTaskTitle.value = ''
    newTaskPomodoros.value = 1
  } catch (error) {
    console.error('添加任务失败:', error)
  }
}

const deleteTask = async (id) => {
  try {
    await taskApi.delete(id)
    tasks.value = tasks.value.filter(t => t.id !== id)
  } catch (error) {
    console.error('删除任务失败:', error)
  }
}

const toggleTaskStatus = async (task) => {
  try {
    const newStatus = task.status === 'completed' ? 'pending' : 'completed'
    const updatedTask = { ...task, status: newStatus }
    
    await taskApi.update(task.id, updatedTask)
    
    const index = tasks.value.findIndex(t => t.id === task.id)
    if (index !== -1) {
      tasks.value[index] = { ...tasks.value[index], status: newStatus }
    }
  } catch (error) {
    console.error('更新任务状态失败:', error)
  }
}

const selectTask = (task) => {
  emit('select-task', task)
}

const startEditTask = (task) => {
  editingTaskId.value = task.id
  editTaskTitle.value = task.title
  editTaskPomodoros.value = task.estimatedPomodoros
}

const saveEditTask = async () => {
  if (!editTaskTitle.value.trim()) return
  
  try {
    const task = tasks.value.find(t => t.id === editingTaskId.value)
    if (task) {
      const updatedTask = {
        ...task,
        title: editTaskTitle.value.trim(),
        estimatedPomodoros: editTaskPomodoros.value
      }
      
      await taskApi.update(task.id, updatedTask)
      
      const index = tasks.value.findIndex(t => t.id === editingTaskId.value)
      if (index !== -1) {
        tasks.value[index] = updatedTask
      }
    }
    cancelEdit()
  } catch (error) {
    console.error('更新任务失败:', error)
  }
}

const cancelEdit = () => {
  editingTaskId.value = null
  editTaskTitle.value = ''
  editTaskPomodoros.value = 1
}

onMounted(() => {
  loadTasks()
})
</script>
