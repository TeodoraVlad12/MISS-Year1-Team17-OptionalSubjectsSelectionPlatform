import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Button,
  TextField,
  FormControl,
  Select,
  MenuItem,
  Alert,
  Snackbar,
  Chip,
  Fab,
  LinearProgress,
  Stack
} from '@mui/material';
import {
  Save as SaveIcon,
  Add as AddIcon,
  Delete as DeleteIcon
} from '@mui/icons-material';
import CourseService, {
  type Course
} from '../../services/CourseService';
import TopBar from '../TopBar/TopBar';

interface LocalRequirement {
  id?: number;
  mandatoryCourseId: number;
  mandatoryName: string;
  percentage: number;
}

interface CourseValidation {
  isValid: boolean;
  message: string;
  requirementCount: number;
  totalPercentage: number;
}

const CourseRequirements: React.FC = () => {
  const [optionalCourses, setOptionalCourses] = useState<Course[]>([]);
  const [mandatoryCourses, setMandatoryCourses] = useState<Course[]>([]);
  const [localRequirements, setLocalRequirements] = useState<{ [courseId: number]: LocalRequirement[] }>({});
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [validationErrors, setValidationErrors] = useState<{ [courseId: number]: CourseValidation }>({});

  useEffect(() => {
    loadData();
  }, []);

  useEffect(() => {
    validateAllCourses();
  }, [localRequirements, optionalCourses]);

  const loadData = async () => {
    try {
      setLoading(true);
      const [optionalData, mandatoryData] = await Promise.all([
        CourseService.getOptionalCourses(),
        CourseService.getMandatoryCourses()
      ]);
      setOptionalCourses(optionalData);
      setMandatoryCourses(mandatoryData);
      
      // Load existing requirements
      const requirementsData: { [courseId: number]: LocalRequirement[] } = {};
      for (const course of optionalData) {
        try {
          const courseReqs = await CourseService.getRequirements(course.id);
          requirementsData[course.id] = courseReqs.map(req => ({
            id: req.id,
            mandatoryCourseId: req.mandatoryId, // Use the ID directly from backend
            mandatoryName: req.mandatoryName,
            percentage: req.percentage
          }));
        } catch {
          requirementsData[course.id] = [];
        }
      }
      setLocalRequirements(requirementsData);
    } catch (err) {
      setError('Failed to load data');
    } finally {
      setLoading(false);
    }
  };

  const validateCourse = (courseId: number): CourseValidation => {
    const requirements = localRequirements[courseId] || [];
    const count = requirements.length;
    const total = requirements.reduce((sum, req) => sum + req.percentage, 0);
    
    if (count === 0) {
      return { isValid: true, message: '', requirementCount: count, totalPercentage: total };
    }
    
    if (count !== 2) {
      return { 
        isValid: false, 
        message: `Must have exactly 2 mandatory courses (currently: ${count})`,
        requirementCount: count,
        totalPercentage: total
      };
    }
    
    if (Math.abs(total - 100) > 0.1) { // Allow for floating point precision
      return { 
        isValid: false, 
        message: `Percentages must total exactly 100% (currently: ${total}%)`,
        requirementCount: count,
        totalPercentage: total
      };
    }
    
    return { isValid: true, message: '', requirementCount: count, totalPercentage: total };
  };

  const validateAllCourses = () => {
    const validations: { [courseId: number]: CourseValidation } = {};
    optionalCourses.forEach(course => {
      validations[course.id] = validateCourse(course.id);
    });
    setValidationErrors(validations);
  };

  const addRequirement = (courseId: number) => {
    const currentReqs = localRequirements[courseId] || [];
    if (currentReqs.length >= 2) {
      setError('Maximum 2 mandatory courses allowed per optional course');
      return;
    }

    const newReq: LocalRequirement = {
      mandatoryCourseId: 0,
      mandatoryName: '',
      percentage: 0
    };

    setLocalRequirements(prev => ({
      ...prev,
      [courseId]: [...currentReqs, newReq]
    }));
  };

  const updateRequirement = (courseId: number, index: number, field: keyof LocalRequirement, value: any) => {
    setLocalRequirements(prev => {
      const courseReqs = [...(prev[courseId] || [])];
      const req = { ...courseReqs[index] };
      
      if (field === 'mandatoryCourseId') {
        const mandatoryCourse = mandatoryCourses.find(m => m.id === value);
        req.mandatoryCourseId = value;
        req.mandatoryName = mandatoryCourse?.name || '';
      } else {
        (req as any)[field] = value;
      }
      
      courseReqs[index] = req;
      return { ...prev, [courseId]: courseReqs };
    });
  };

  const removeRequirement = (courseId: number, index: number) => {
    setLocalRequirements(prev => {
      const courseReqs = [...(prev[courseId] || [])];
      courseReqs.splice(index, 1);
      return { ...prev, [courseId]: courseReqs };
    });
  };

  const getAvailableMandatoryCourses = (courseId: number, currentIndex: number) => {
    const courseReqs = localRequirements[courseId] || [];
    const usedIds = courseReqs
      .map((req, idx) => idx !== currentIndex ? req.mandatoryCourseId : 0)
      .filter(id => id > 0);
    
    return mandatoryCourses.filter(course => !usedIds.includes(course.id));
  };

  const saveAllRequirements = async () => {
    setSaving(true);
    try {
      // Validate before saving
      const hasErrors = Object.values(validationErrors).some(v => !v.isValid);
      if (hasErrors) {
        setError('Cannot save until requirements are met for all courses!');
        return;
      }

      // Process each course individually with rollback on failure
      for (const course of optionalCourses) {
        const courseReqs = localRequirements[course.id] || [];
        let backupRequirements: any[] = [];
        
        try {
          // Step 1: Backup existing requirements for rollback
          backupRequirements = await CourseService.getRequirements(course.id);
          
          // Step 2: Delete ALL existing requirements for this course
          for (const existingReq of backupRequirements) {
            await CourseService.deleteRequirement(existingReq.id);
          }

          // Step 3: Create new requirements (only valid ones)
          const newRequirements = [];
          for (const req of courseReqs) {
            if (req.mandatoryCourseId > 0 && req.percentage > 0) {
              const newReq = await CourseService.createRequirement(course.id, {
                mandatoryCourseId: req.mandatoryCourseId,
                percentage: req.percentage
              });
              newRequirements.push(newReq);
            }
          }
          
          // Success for this course
          console.log(`Successfully updated requirements for ${course.name}`);
          
        } catch (courseError: any) {
          console.error(`Failed to update ${course.name}:`, courseError);
          
          // Rollback: Try to restore backup requirements
          try {
            for (const backupReq of backupRequirements) {
              await CourseService.createRequirement(course.id, {
                mandatoryCourseId: backupReq.mandatoryId,
                percentage: backupReq.percentage
              });
            }
            console.log(`Rollback successful for ${course.name}`);
          } catch (rollbackError) {
            console.error(`Rollback failed for ${course.name}:`, rollbackError);
          }
          
          // Re-throw the original error
          throw new Error(`Failed to save requirements for ${course.name}: ${courseError.message}`);
        }
      }

      setMessage('All course requirements saved successfully!');
      // Reload to get fresh IDs and sync state
      await loadData();
    } catch (err: any) {
      setError(err.message || 'Failed to save requirements');
      // Reload to show current database state
      await loadData();
    } finally {
      setSaving(false);
    }
  };

  return (
    <Box>
      <TopBar 
        title="ElectiveMatch - Course Requirements"
        showHomeButton={true}
      />

      <Box sx={{ p: 3, pb: 10 }}>
        <Typography variant="h4" gutterBottom>
          Course Requirements Management
        </Typography>
        
        <Alert severity="info" sx={{ mb: 3 }}>
          <Typography variant="body1" sx={{ fontWeight: 'bold' }}>Rules:</Typography>
          <Typography variant="body2">
            • Each optional course must have either 0 requirements OR exactly 2 mandatory courses<br/>
            • When 2 courses are selected, their percentages must total exactly 100%<br/>
            • Use the Save button to apply all changes
          </Typography>
        </Alert>

        {loading && <LinearProgress sx={{ mb: 2 }} />}

        <Box sx={{ maxWidth: '100%' }}>
          {optionalCourses.map((course) => {
            const validation = validationErrors[course.id];
            const requirements = localRequirements[course.id] || [];
            const hasError = validation && !validation.isValid;

            return (
              <Card 
                key={course.id}
                sx={{ 
                  mb: 3,
                  border: hasError ? '2px solid #f44336' : '1px solid #e0e0e0',
                  backgroundColor: hasError ? '#ffebee' : 'white'
                }}
              >
                <CardContent>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                    <Box>
                      <Typography variant="h6" color={hasError ? 'error' : 'inherit'}>
                        {course.name}
                      </Typography>
                      <Box sx={{ display: 'flex', gap: 1, alignItems: 'center', mt: 1 }}>
                        <Chip label={course.code} size="small" />
                        {validation && (
                          <Typography variant="caption" color={hasError ? 'error' : 'text.secondary'}>
                            Requirements: {validation.requirementCount}/2 | Total: {validation.totalPercentage}%
                          </Typography>
                        )}
                      </Box>
                    </Box>
                    <Button
                      variant="outlined"
                      startIcon={<AddIcon />}
                      onClick={() => addRequirement(course.id)}
                      size="small"
                      disabled={requirements.length >= 2}
                    >
                      Add Requirement
                    </Button>
                  </Box>

                  {hasError && (
                    <Alert severity="error" sx={{ mb: 2 }}>
                      {validation.message}
                    </Alert>
                  )}

                  {requirements.map((req, index) => (
                    <Box key={index} sx={{ mb: 2, p: 3, border: '1px solid #ddd', borderRadius: 1 }}>
                      <Stack 
                        direction={{ xs: 'column', sm: 'row' }}
                        spacing={3}
                        sx={{ alignItems: { xs: 'stretch', sm: 'flex-start' } }}
                      >
                        <Box sx={{ minWidth: 280, pt: 1 }}>
                          <Typography variant="body2" sx={{ mb: 1, color: 'text.secondary' }}>
                            Mandatory Course
                          </Typography>
                          <FormControl fullWidth size="small">
                            <Select
                              value={req.mandatoryCourseId || ''}
                              onChange={(e) => updateRequirement(course.id, index, 'mandatoryCourseId', Number(e.target.value))}
                            >
                              {getAvailableMandatoryCourses(course.id, index).map((mandatoryCourse) => (
                                <MenuItem key={mandatoryCourse.id} value={mandatoryCourse.id}>
                                  {mandatoryCourse.name}
                                </MenuItem>
                              ))}
                            </Select>
                          </FormControl>
                        </Box>
                        
                        <Box sx={{ minWidth: 120, pt: 1 }}>
                          <Typography variant="body2" sx={{ mb: 1, color: 'text.secondary' }}>
                            Percentage
                          </Typography>
                          <TextField
                            type="number"
                            size="small"
                            fullWidth
                            value={req.percentage || ''}
                            onChange={(e) => updateRequirement(course.id, index, 'percentage', Number(e.target.value))}
                            inputProps={{ min: 0, max: 100, step: 0.1 }}
                            placeholder="0-100"
                          />
                        </Box>
                        
                        <Box sx={{ pt: 1, alignSelf: { xs: 'flex-start', sm: 'flex-start' } }}>
                          <Typography variant="body2" sx={{ mb: 1, color: 'transparent' }}>
                            Action
                          </Typography>
                          <Button
                            variant="outlined"
                            color="error"
                            startIcon={<DeleteIcon />}
                            onClick={() => removeRequirement(course.id, index)}
                            size="small"
                            sx={{ height: 40 }}
                          >
                            Remove
                          </Button>
                        </Box>
                      </Stack>
                    </Box>
                  ))}

                  {requirements.length === 0 && (
                    <Typography variant="body2" color="text.secondary">
                      No requirements set for this course
                    </Typography>
                  )}
                </CardContent>
              </Card>
            );
          })}
        </Box>

        {/* Global Save Button */}
        <Fab
          color="primary"
          aria-label="save"
          sx={{
            position: 'fixed',
            bottom: 16,
            right: 16,
          }}
          onClick={saveAllRequirements}
          disabled={saving || loading}
        >
          <SaveIcon />
        </Fab>

        <Snackbar
          open={!!message}
          autoHideDuration={6000}
          onClose={() => setMessage('')}
        >
          <Alert severity="success" onClose={() => setMessage('')}>
            {message}
          </Alert>
        </Snackbar>

        <Snackbar
          open={!!error}
          autoHideDuration={6000}
          onClose={() => setError('')}
        >
          <Alert severity="error" onClose={() => setError('')}>
            {error}
          </Alert>
        </Snackbar>
      </Box>
    </Box>
  );
};

export default CourseRequirements;
