<template>
  <div class="card">
    <div class="mode-tabs">
      <div 
        :class="['mode-tab', { active: currentMode === 'work' }]"
        @click="switchMode('work')"
      >
        专注模式
      </div>
      <div 
        :class="['mode-tab', { active: currentMode === 'break' }]"
        @click="switchMode('break')"
      >
        休息模式
      </div>
    </div>

    <div class="timer-container">
      <div v-if="selectedTask" class="selected-task-info" style="margin-bottom: 20px; color: var(--text-secondary);">
        当前任务: <strong>{{ selectedTask.title }}</strong>
        <span style="margin-left: 8px;">
          ({{ selectedTask.completedPomodoros }}/{{ selectedTask.estimatedPomodoros }})
        </span>
      </div>
      
      <div class="timer-display">{{ formatTime }}</div>
      
      <div class="timer-controls">
        <button 
          v-if="!isRunning"
          class="btn btn-primary btn-lg"
          @click="startTimer"
        >
          {{ remainingTime === totalSeconds ? '开始' : '继续' }}
        </button>
        <button 
          v-if="isRunning"
          class="btn btn-outline btn-lg"
          @click="pauseTimer"
        >
          暂停
        </button>
        <button 
          class="btn btn-ghost btn-lg"
          @click="resetTimer"
        >
          重置
        </button>
        <button 
          v-if="currentMode === 'break' && isRunning"
          class="btn btn-secondary btn-lg"
          @click="skipBreak"
        >
          跳过休息
        </button>
      </div>
    </div>

    <div class="timer-settings">
      <div class="timer-setting-item">
        <span>专注时长:</span>
        <input 
          type="number" 
          v-model.number="localWorkDuration"
          min="1"
          max="120"
          @change="updateDurations"
        />
        <span>分钟</span>
      </div>
      <div class="timer-setting-item">
        <span>休息时长:</span>
        <input 
          type="number" 
          v-model.number="localBreakDuration"
          min="1"
          max="30"
          @change="updateDurations"
        />
        <span>分钟</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { recordApi } from '../api/index.js'
import { showNotification, playNotificationSound } from '../utils/notification.js'

const props = defineProps({
  workDuration: {
    type: Number,
    default: 25
  },
  breakDuration: {
    type: Number,
    default: 5
  },
  selectedTask: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['task-completed', 'update-document-title'])

const currentMode = ref('work')
const isRunning = ref(false)
const localWorkDuration = ref(props.workDuration)
const localBreakDuration = ref(props.breakDuration)
const remainingTime = ref(props.workDuration * 60)
const startTime = ref(null)
const timerInterval = ref(null)

const totalSeconds = computed(() => {
  return currentMode.value === 'work' 
    ? localWorkDuration.value * 60 
    : localBreakDuration.value * 60
})

const formatTime = computed(() => {
  const minutes = Math.floor(remainingTime.value / 60)
  const seconds = remainingTime.value % 60
  return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
})

const updateDurations = () => {
  if (!isRunning.value) {
    remainingTime.value = totalSeconds.value
  }
}

const switchMode = (mode) => {
  if (isRunning.value) {
    pauseTimer()
  }
  currentMode.value = mode
  remainingTime.value = totalSeconds.value
  updateDocumentTitle()
}

const startTimer = () => {
  isRunning.value = true
  startTime.value = new Date()
  
  timerInterval.value = setInterval(() => {
    if (remainingTime.value > 0) {
      remainingTime.value--
      updateDocumentTitle()
    } else {
      completeTimer()
    }
  }, 1000)
}

const pauseTimer = () => {
  isRunning.value = false
  if (timerInterval.value) {
    clearInterval(timerInterval.value)
    timerInterval.value = null
  }
}

const resetTimer = () => {
  pauseTimer()
  remainingTime.value = totalSeconds.value
  startTime.value = null
  updateDocumentTitle()
}

const skipBreak = () => {
  pauseTimer()
  switchMode('work')
}

const completeTimer = async () => {
  pauseTimer()
  
  if (currentMode.value === 'work') {
    const endTime = new Date()
    const duration = Math.round((endTime - startTime.value) / 60000)
    
    try {
      const recordData = {
        taskId: props.selectedTask?.id || null,
        taskTitle: props.selectedTask?.title || '未关联任务',
        startTime: startTime.value.toISOString(),
        endTime: endTime.toISOString(),
        duration: duration,
        isCompleted: true,
        type: 'work'
      }
      
      await recordApi.create(recordData)
      
      if (props.selectedTask) {
        emit('task-completed', recordData)
      }
    } catch (error) {
      console.error('记录番茄钟失败:', error)
    }
    
    showNotification('🍅 专注完成!', {
      body: `太棒了！你完成了一个${localWorkDuration.value}分钟的专注。休息一下吧！`
    })
    playNotificationSound()
    
    switchMode('break')
  } else {
    showNotification('☕ 休息结束!', {
      body: '休息时间到了，准备好开始下一个番茄钟了吗？'
    })
    playNotificationSound()
    
    switchMode('work')
  }
}

const updateDocumentTitle = () => {
  const modeText = currentMode.value === 'work' ? '专注' : '休息'
  emit('update-document-title', `${formatTime.value} - ${modeText} - 番茄计时器`)
}

watch(() => props.workDuration, (newVal) => {
  localWorkDuration.value = newVal
  if (currentMode.value === 'work' && !isRunning.value) {
    remainingTime.value = totalSeconds.value
  }
})

watch(() => props.breakDuration, (newVal) => {
  localBreakDuration.value = newVal
  if (currentMode.value === 'break' && !isRunning.value) {
    remainingTime.value = totalSeconds.value
  }
})

onMounted(() => {
  updateDocumentTitle()
})

onUnmounted(() => {
  if (timerInterval.value) {
    clearInterval(timerInterval.value)
  }
  emit('update-document-title', '番茄计时器')
})
</script>
