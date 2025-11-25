import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Button,
  Alert,
  CircularProgress,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Chip
} from '@mui/material';
import { Save as SaveIcon } from '@mui/icons-material';
import { useAuth } from '../../contexts/AuthContext';
import { useServices } from '../../services/ServicesContext';
import TopBar from '../TopBar/TopBar';
import type { OptionalCourse } from '../../models/OptionalCourse';

interface CoursePreference {
  courseId: number;
  priority: number;
}

const StudentPreferences: React.FC = () => {
  const { user } = useAuth();
  const { allocationService } = useServices();
  const [courses, setCourses] = useState<OptionalCourse[]>([]);
  const [preferences, setPreferences] = useState<CoursePreference[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    loadCourses();
  }, []);

  const loadCourses = async () => {
    if (!user?.academicYear || !user?.specialization) return;
    
    try {
      setLoading(true);
      const coursesData = await allocationService.getOptionalCourses(
        // user.academicYear,
        // user.specialization
      );
      setCourses(coursesData);
    } catch (err) {
      setError('Failed to load optional courses');
    } finally {
      setLoading(false);
    }
  };

  const handlePriorityChange = (courseId: number, priority: number) => {
    setPreferences(prev => {
      const filtered = prev.filter(p => p.courseId !== courseId);
      if (priority > 0) {
        return [...filtered, { courseId, priority }].sort((a, b) => a.priority - b.priority);
      }
      return filtered;
    });
  };

  const getPriorityForCourse = (courseId: number): number => {
    return preferences.find(p => p.courseId === courseId)?.priority || 0;
  };

  const getUsedPriorities = (): number[] => {
    return preferences.map(p => p.priority);
  };

  const handleSave = async () => {
    if (preferences.length === 0) {
      setError('Please select at least one course preference');
      return;
    }

    try {
      setSaving(true);
      setError(null);
      await allocationService.saveStudentPreferences(preferences);
      setSuccess(true);
      setTimeout(() => setSuccess(false), 3000);
    } catch (err) {
      setError('Failed to save preferences');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" p={4}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      <TopBar 
        title="Course Preferences"
        showRoleChip={true}
      />
      
      <Box p={3}>
      <Typography variant="h4" gutterBottom>
        Optional Course Preferences
      </Typography>
      
      <Typography variant="body1" color="textSecondary" sx={{ mb: 3 }}>
        Select your preferred optional courses in order of priority. Priority 1 is your most preferred course.
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      {success && (
        <Alert severity="success" sx={{ mb: 2 }}>
          Preferences saved successfully!
        </Alert>
      )}

      <Box sx={{ mb: 3 }}>
        {courses.map((course) => (
          <Card key={course.id} sx={{ mb: 2 }}>
            <CardContent>
              <Box display="flex" justifyContent="space-between" alignItems="center">
                <Box flex={1}>
                  <Typography variant="h6">
                    {course.name}
                  </Typography>
                  <Typography variant="body2" color="textSecondary">
                    Code: {course.code} • Max Students: {course.maxStudents}
                  </Typography>
                </Box>
                
                <Box sx={{ minWidth: 120, ml: 2 }}>
                  <FormControl fullWidth size="small">
                    <InputLabel>Priority</InputLabel>
                    <Select
                      value={getPriorityForCourse(course.id)}
                      label="Priority"
                      onChange={(e) => handlePriorityChange(course.id, Number(e.target.value))}
                    >
                      <MenuItem value={0}>None</MenuItem>
                      {[1, 2, 3, 4, 5].map(priority => (
                        <MenuItem 
                          key={priority} 
                          value={priority}
                          disabled={getUsedPriorities().includes(priority) && getPriorityForCourse(course.id) !== priority}
                        >
                          {priority}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Box>
              </Box>
            </CardContent>
          </Card>
        ))}
      </Box>

      {preferences.length > 0 && (
        <Card sx={{ mb: 3, bgcolor: 'grey.50' }}>
          <CardContent>
            <Typography variant="h6" gutterBottom>
              Your Preferences Summary
            </Typography>
            <Box display="flex" flexWrap="wrap" gap={1}>
              {preferences
                .sort((a, b) => a.priority - b.priority)
                .map((pref) => {
                  const course = courses.find(c => c.id === pref.courseId);
                  return (
                    <Chip
                      key={pref.courseId}
                      label={`${pref.priority}. ${course?.name}`}
                      color="primary"
                      variant="outlined"
                    />
                  );
                })}
            </Box>
          </CardContent>
        </Card>
      )}

      <Button
        variant="contained"
        size="large"
        startIcon={<SaveIcon />}
        onClick={handleSave}
        disabled={saving || preferences.length === 0}
      >
        {saving ? <CircularProgress size={20} /> : 'Save Preferences'}
      </Button>
      </Box>
    </Box>
  );
};

export default StudentPreferences;
